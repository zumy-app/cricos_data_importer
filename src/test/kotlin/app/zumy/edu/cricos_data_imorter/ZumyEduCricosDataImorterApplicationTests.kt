package app.zumy.edu.cricos_data_imorter

import com.google.firebase.cloud.FirestoreClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ZumyEduCricosDataImorterApplicationTests {

    @Test
    fun testCreateDocument(){
        val db = FirestoreClient.getFirestore()
        val doc = db.collection("data").document("2023-01-03")
        Assertions.assertEquals("2023-01-03", doc.id)
    }
}
