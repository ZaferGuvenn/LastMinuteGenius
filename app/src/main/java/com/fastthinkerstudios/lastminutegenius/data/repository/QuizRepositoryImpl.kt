import com.fastthinkerstudios.lastminutegenius.data.local.dao.QuizDao
import com.fastthinkerstudios.lastminutegenius.data.local.dao.VideoDao
import com.fastthinkerstudios.lastminutegenius.data.local.entity.QuizEntity
import com.fastthinkerstudios.lastminutegenius.data.remote.api.QuizApi
import com.fastthinkerstudios.lastminutegenius.domain.repository.QuizRepository
import com.fastthinkerstudios.lastminutegenius.presentation.quiz.Quiz
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import org.json.JSONArray

class QuizRepositoryImpl @Inject constructor(
    private val quizApi: QuizApi,
    private val quizDao: QuizDao,
    private val videoDao: VideoDao
) : QuizRepository {

    override suspend fun getQuiz(videoId: Int): List<Quiz> {
        // Eğer DB'de varsa onu döndür
        val cached = quizDao.getQuizForVideo(videoId)
        if (cached.isNotEmpty()) {
            return cached.map { it.toDomain() }
        }

        // Cache'de yoksa yeni quiz oluştur
        return generateNewQuiz(videoId)
    }

    override suspend fun generateNewQuiz(videoId: Int): List<Quiz> {
        // Önce eski quiz'leri sil
        quizDao.deleteQuizForVideo(videoId)

        // Summary metnini al
        val summary = videoDao.getSummaryByVideoId(videoId).firstOrNull() ?: throw Exception("Özet bulunamadı")
        
        // API çağrısı
        val response = quizApi.generateQuiz(summary)
        val json = JSONArray(response.quiz)

        println("summary")
        println(summary)
        println("response")
        println(response)

        // Parse edip liste oluştur
        val quizzes = mutableListOf<QuizEntity>()
        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            val options = obj.getJSONArray("secenekler")
            val optionList = (0 until options.length()).map { options.getString(it) }

            quizzes.add(
                QuizEntity(
                    videoId = videoId,
                    question = obj.getString("soru"),
                    options = optionList,
                    correctAnswer = obj.getString("dogru")
                )
            )
        }

        // DB'ye kaydet
        quizDao.insertAll(quizzes)

        return quizzes.map { it.toDomain() }
    }
}
