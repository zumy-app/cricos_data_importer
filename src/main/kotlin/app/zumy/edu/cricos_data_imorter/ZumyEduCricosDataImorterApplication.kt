package app.zumy.edu.cricos_data_imorter

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream

@SpringBootApplication
class ZumyEduCricosDataImorterApplication

fun main(args: Array<String>) {

    val filepath = "src/main/resources/cricos-providers-courses-and-locations-as-at-2022-10-4-11-02-16.xlsx"
    readFromExcelFile(filepath)
    runApplication<ZumyEduCricosDataImorterApplication>(*args)
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
    val locations = parseCourses(xlWb.getSheetAt(2))
    val course_locations = parseCourses(xlWb.getSheetAt(2))
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
        r!!.getCell(18).numericCellValue,
        r!!.getCell(19).numericCellValue,
        r!!.getCell(20).numericCellValue,
        r!!.getCell(21).numericCellValue,
        if(r!!.getCell(22).stringCellValue=="No") false else true,
        Currency.AUD
        )

}

class Institution (
    val cricos_provider_code: String,
    val trading_name:String,
    val institution_name:String,
    val type: InstitutionType,
    val capacity:Double,
    val website:String,
    val address: Address,
    val  locations: MutableList<Location>,
        ) {
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
)

enum class Country {
AUS
}

class Course (
    //foreign
    val cricos_provider_code: String,
    //Primary
    val cricos_course_code: String,
    val course_name:String,
    val vet_national_code: String,
    val dual_qualification:Boolean,
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
    val work_component:Boolean,
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
)

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


class Location(
    name:String,
    primary:Boolean,
    type: Location_Type,
    address: Address,
)

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
