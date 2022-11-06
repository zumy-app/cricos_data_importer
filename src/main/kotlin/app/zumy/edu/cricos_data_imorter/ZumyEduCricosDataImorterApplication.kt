package app.zumy.edu.cricos_data_imorter

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
    val xlWs = xlWb.getSheetAt(0)
    println(xlWs.getRow(rowNumber).getCell(columnNumber))
}

class Institution (
    val cricos_provider_code: String,
    val trading_name:String,
    val institution_name:String,
    val capacity:Int,
    val type: InstitutionType,
    val website:String,
    val address: Address
        ) {
    enum class InstitutionType {
    GOV,PRIVATE
    }
}
class Address(
    val line1:String,
    val line2:String,
    val city:String,
    val state:String,
    val country:Country,
    val postal:Country,
)

enum class Country {
AUSTRALIA
}

class Course (
    val cricos_provider_code: String,
    val cricos_course_code: String,
    val course_name:String,
    val vet_national_code: String,
    val dual_qualification:Boolean,
    //todo
    val field_of_education:Boolean,
    val course_level:Course_Level,
    val foundation_studies:Boolean,
    val work_component:Boolean,
    val work_component_hours_per_week:Int,
    val language:Language,
    val duration:Int,
    val tuition_fee:Int,
    val non_tuition:Int,
    val estimated_course_total:Int,
    val expired:Boolean,
    val currency:Currency,
    val institution_name:String,
    val capacity:Int,
    val type: InstitutionType,
    val website:String,
    val address: Address
) {
    enum class InstitutionType {
        GOV,PRIVATE
    }
}

enum class Currency {

}

enum class Language {

}

enum class Course_Level {

}
