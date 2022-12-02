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
    val xlWs = xlWb.getSheetAt(1)
    println(xlWs.getRow(rowNumber).getCell(columnNumber))
    sheetToMap(xlWs)
}

fun sheetToMap(s: Sheet){
val institutions = mutableListOf<Institution>()
    s.map { r ->
        if(r.rowNum>2) {

            if(r!!.getCell(0)!=null)
            {
            val i = createInstitution(r)
        println("Adding ${i.cricos_provider_code}")
            institutions.add(i)

            }
        }
    }
    print(institutions.size)
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
    val field_of_education:Boolean,
    val course_level:Course_Level,
    val foundation_studies:Boolean,
    val work_component:Boolean,
    val work_component_hours_per_week:Float,
    val work_component_weeks:Int,
    val work_component_total_hours:Int,
    val language:Language,
    val duration:Int,
    val tuition_fee:Float,
    val non_tuition:Int,
    val estimated_course_total:Int,
    val expired:Boolean,
    val currency:Currency
)


class Location(
    name:String,
    primary:Boolean,
    type: Location_Type,
    address: Address,
)

enum class Location_Type(val locatoin:String) {
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
