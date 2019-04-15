package tauch.xavier.go4lunch.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import tauch.xavier.go4lunch.models.User

private const val COLLECTION_NAME = "users"

object UserHelper /*CRUD requests on "users" collection*/ {

        // --- COLLECTION REFERENCE ---

        private fun getUsersCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
        }

        // --- CREATE ---

        fun createUser(uid: String, username: String?, email: String?, urlPicture: String?): Task<Void> {
            val userToCreate = User(uid, username, email, urlPicture)
            return getUsersCollection().document(uid).set(userToCreate)
        }

        // --- GET ---

        fun getUser(uid: String): Task<DocumentSnapshot> {
            return getUsersCollection().document(uid).get()
        }

        // --- UPDATE ---

        fun updateUsername(uid: String, username: String): Task<Void> {
            return getUsersCollection().document(uid).update("username", username)
        }

        // --- DELETE ---

        fun deleteUser(uid: String): Task<Void> {
            return getUsersCollection().document(uid).delete()
        }
}