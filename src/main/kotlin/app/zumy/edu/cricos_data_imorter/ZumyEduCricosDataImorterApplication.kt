package app.zumy.edu.cricos_data_imorter

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FieldValue
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import java.io.FileInputStream
import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.annotation.PostConstruct


@SpringBootApplication
@EnableAutoConfiguration(exclude = [
    DataSourceAutoConfiguration::class,
    DataSourceTransactionManagerAutoConfiguration::class,
    HibernateJpaAutoConfiguration::class])
class ZumyEduCricosDataImorterApplication

fun main(args: Array<String>) {
    val classLoader = Thread.currentThread().contextClassLoader
    val serviceAccount = classLoader.getResourceAsStream("service-account-file.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)

    val filepath = "src/main/resources/cricos-providers-courses-and-locations-as-at-2023-1-3-7-59-17.xlsx"
    readFromExcelFile(filepath)
    runApplication<ZumyEduCricosDataImorterApplication>(*args)
}

@Service
class FBInitialize {
    @PostConstruct
    fun initialize() {
        try {

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
            FirebaseApp.initializeApp(options)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun readFromExcelFile(filepath: String) {
    val inputStream = FileInputStream(filepath)
    //Instantiate Excel workbook using existing file:
    var xlWb = WorkbookFactory.create(inputStream)

    //Row index specifies the row in the worksheet (starting at 0):
    val rowNumber = 0
    //Cell index specifies the column within the chosen row (starting at 0):
    val columnNumber = 0

    //Get reference to first sheet:

    val institutions = parseInstitutions(xlWb.getSheetAt(1))
    val courses = parseCourses(xlWb.getSheetAt(2))
    val locations = parseLocations(xlWb.getSheetAt(3))
    val courseLocations: MutableList<CourseLocation> = parseCourseLocations(xlWb.getSheetAt(4))

    //Create initial state of the db i.e create empty collections for institutions, courses, locations, course_locations
    //separate for each db
    val version = "2023-01-03"
    prepareDB(version)

    //function that starts saving all the data
    save()
    println("Processing complete")
}

fun save(){
    /*    saveInstitutions(institutions)
    saveCourses( courses)
    saveLocations( locations)
    saveCourseLocations(courseLocations)*/
}

//create an empty collection for the version and within
fun prepareDB(version: String){
    val db = FirestoreClient.getFirestore()

    db.collection("data").document(version)

}

fun saveInstitutions(ins:MutableList<Institution>){
    ins.forEach{
        i-> writeToDB("institutions", i.cricosProviderCode, i)
    }
}
fun saveCourses(ins:List<Course>){
    ins.forEach{
            i-> writeToDB("courses", i.cricosCourseCode, i)
    }
}

fun saveLocations(ins:MutableList<Location>){
    val db = FirestoreClient.getFirestore()
    ins.forEach{
            i-> writeToDB("locations", i.name, i)
    }
}

fun saveCourseLocations(ins:MutableList<CourseLocation>){
    ins.forEach{
            i-> writeToDB("course_locations", i.cricosProviderCode, i)
    }
}

fun writeToDB(coll: String, docName: String, data: Any){
val db = FirestoreClient.getFirestore()
    db.collection(coll).document(docName).set(data)
}

fun parseCourseLocations(s: Sheet): MutableList<CourseLocation> {
    val courseLocations = mutableListOf<CourseLocation>()
    s.map { r ->
        if(r.rowNum>2) {
            if(r!!.getCell(0)!=null)
            {
                val i = createCourseLocation(r)
                courseLocations.add(i)
            }
        }
    }
    print("Parsed ${courseLocations.size} courseLocations")

    return courseLocations
}

fun createCourseLocation(r: Row): CourseLocation {
    return CourseLocation(
        r!!.getCell(0).stringCellValue,
        r!!.getCell(2).stringCellValue,
        r!!.getCell(3).stringCellValue,
        r!!.getCell(4).stringCellValue,
        r!!.getCell(5).stringCellValue,
        Country.AUS
    )
}


fun parseInstitutions(s: Sheet): MutableList<Institution> {
val institutions = mutableListOf<Institution>()
    s.map { r ->
        if(r.rowNum>2) {
            if(r!!.getCell(0)!=null)
            {
            val i = createInstitution(r)
            institutions.add(i)
            }
        }
    }
    print("Parsed ${institutions.size} institutions")

    return institutions
}

fun createInstitution(r: Row?): Institution {

    return Institution(
        r!!.getCell(0).stringCellValue,
        r!!.getCell(1).stringCellValue,
        r!!.getCell(2).stringCellValue,
        if(r!!.getCell(3).stringCellValue=="Government") InstitutionType.GOV else InstitutionType.PRIVATE,
        r!!.getCell(4).numericCellValue,
        r!!.getCell(5).stringCellValue,
        Address(
            r!!.getCell(6).stringCellValue,
            r!!.getCell(6).stringCellValue,
            r!!.getCell(6).stringCellValue,
            r!!.getCell(6).stringCellValue,
            r!!.getCell(6).stringCellValue,
            r!!.getCell(6).stringCellValue,
            r!!.getCell(6).stringCellValue,
            Country.AUS
        ),
mutableListOf()
    )

}

fun parseCourses(s: Sheet): List<Course> {
    val courses = mutableListOf<Course>()
    s.map { r ->
        if(r.rowNum>2) {
            if(r!!.getCell(0)!=null)
            {
                val i = createCourse(r)
                courses.add(i)
            }
        }
    }
    print("Parsed ${courses.size} courses")

    return courses
}

fun createCourse(r: Row?): Course {

    return Course(
        r!!.getCell(0).stringCellValue,
        r!!.getCell(2).stringCellValue,
        r!!.getCell(3).stringCellValue,
        r!!.getCell(4).stringCellValue,
        if(r!!.getCell(5).stringCellValue=="No") false else true,
        r!!.getCell(6).stringCellValue,
        r!!.getCell(7).stringCellValue,
        r!!.getCell(8).stringCellValue,
        r!!.getCell(9).stringCellValue,
        r!!.getCell(10).stringCellValue,
        r!!.getCell(11).stringCellValue,
        r!!.getCell(12).stringCellValue,
        if(r!!.getCell(13).stringCellValue=="No") false else true,
        if(r!!.getCell(14).stringCellValue=="No") false else true,
        r!!.getCell(15).numericCellValue,
        r!!.getCell(16).numericCellValue,
        r!!.getCell(17).numericCellValue,
        Language.ENGLISH,
        r!!.getCell(19).numericCellValue,
        parseNumber(r!!.getCell(20)),
        parseNumber(r!!.getCell(21)),
        r!!.getCell(22).numericCellValue,
        if(r!!.getCell(23).stringCellValue=="No") false else true,
        Currency.AUD
        )

}

fun parseNumber(c: Cell): Double {
   try{
       return c.numericCellValue
   }
   catch(e: IllegalStateException){
       return 0.0
   }
}

fun parseLocations(s: Sheet): MutableList<Location> {
    val locations = mutableListOf<Location>()
    s.map { r ->
        if(r.rowNum>2) {
            if(r!!.getCell(0)!=null)
            {
                val i = createLocation(r)
                locations.add(i)
            }
        }
    }
    print("Parsed ${locations.size} locations")

    return locations
}

fun createLocation(r: Row): Location {
    return Location(
        r!!.getCell(0).stringCellValue,
        r!!.getCell(2).stringCellValue,
        r!!.getCell(3).stringCellValue,
        Address(
            r!!.getCell(4).stringCellValue,
            r!!.getCell(5).stringCellValue,
            r!!.getCell(6).stringCellValue,
            r!!.getCell(7).stringCellValue,
            r!!.getCell(8).stringCellValue,
            r!!.getCell(9).stringCellValue,
            r!!.getCell(6).stringCellValue,
            Country.AUS
        )
    )
}

data class Institution(
    val cricosProviderCode: String,
    val trading_name:String,
    val institution_name:String,
    val type: InstitutionType,
    val capacity:Double,
    val website:String,
    val address: Address,
    val  locations: MutableList<Location>,
        ) : Serializable {
}
    enum class InstitutionType {
    GOV,PRIVATE
    }
class Address(
    val line1:String,
    val line2:String,
    val line3:String,
    val line4:String,
    val city:String,
    val state:String,
    val postal:String,
    val country:Country,
): Serializable

enum class Country {
AUS
}

class Course (
    //foreign
    val cricosProviderCode: String,
    //Primary
    val cricosCourseCode: String,
    val name:String,
    val vetNationalCode: String,
    val dualQualification:Boolean,
    /*
    Field of Education Table - Level 1

09 - Society & culture


Field of Education Level 2
0907 - Behavioral Science
0909 - Law
0919- -Ecomomics and Econometrics



Field of Education Level 3
090900 - Law, n.f.d
090909 - International Law


     */
    val field_of_education_1_braod:String,
    val field_of_education_1_narrow:String,
    val field_of_education_1_detailed:String,
    val field_of_education_2_braod:String,
    val field_of_education_2_narrow:String,
    val field_of_education_2_detailed:String,
    val course_level:String,
    val foundation_studies:Boolean,
    val work_component:Boolean, //14
    val work_component_hours_per_week:Double,
    val work_component_weeks:Double,
    val work_component_total_hours:Double,
    val language:Language,
    val duration:Double,
    val tuition_fee:Double,
    val non_tuition:Double,
    val estimated_course_total:Double,
    val expired:Boolean,
    val currency:Currency
): Serializable

/*class FieldOfEducationL1(
    val id: Double,
    val name:String,
    val child: MutableList<FieldOfEducationL2>
)

class FieldOfEducationL2(
    val id: Double,
    val name:String,
    val child: MutableList<FieldOfEducationL3>
)

class FieldOfEducationL3(
    val id: Double,
    val name:String
)*/


data class Location(
   val cricosProviderCode: String,
   val name: String,
   val  type: String,
   val  address: Address,
): Serializable

data class CourseLocation(
    val cricosProviderCode:String,
    val cricosCourseCode: String,
    val locationName:String,
    val city:String,
    val state:String,
    val  country: Country
): Serializable
enum class Location_Type(val location:String) {
OWNED("Location owned and operated by provider"),
REG_PROVIDER("Arrangement with other registered provider"),
NON_REG_PROVIDER("Arrangement with Non registered provider")
}

enum class Currency {
AUD,USD
}

enum class Language {
ENGLISH
}

enum class Course_Level(val level:String) {
DIPLOMA("Diploma"),
SEC_CERT("Senior Secondary Certificate of Education"),
    CERTIII("Certificate III")
}

