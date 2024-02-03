package com.jetpackcomposethe.justmeet

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTarget
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.jetpackcomposethe.justmeet.data.CHATS
import com.jetpackcomposethe.justmeet.data.ChatData
import com.jetpackcomposethe.justmeet.data.ChatUser
import com.jetpackcomposethe.justmeet.data.Event
import com.jetpackcomposethe.justmeet.data.MESSAGE
import com.jetpackcomposethe.justmeet.data.Message
import com.jetpackcomposethe.justmeet.data.STATUS
import com.jetpackcomposethe.justmeet.data.Status
import com.jetpackcomposethe.justmeet.data.USER_NODE
import com.jetpackcomposethe.justmeet.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.w3c.dom.Text
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth, var db: FirebaseFirestore, val storage: FirebaseStorage
) : ViewModel() {


    var inProcess = mutableStateOf(false)
    var inprocesschats = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Event<String>?>(null)
    var signin = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    // Chats

    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListner: ListenerRegistration? = null

    // Status

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)


    init {

        val currentUser = auth.currentUser
        signin.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }


    fun populateMessages(chatID: String) {
        inProgressChatMessage.value = true
        currentChatMessageListner =
            db.collection(CHATS).document(chatID).collection(MESSAGE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error)
                    }
                    if (value != null) {
                        chatMessages.value = value.documents.mapNotNull {
                            it.toObject<Message>()
                        }.sortedBy { it.timestamp }
                        inProgressChatMessage.value = false
                    }
                }
    }

    fun DepopulateMessage() {
        chatMessages.value = listOf()
        currentChatMessageListner = null
    }

    fun populateChats() {
        inprocesschats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)

            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)

            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inprocesschats.value = false
            }
        }
    }

    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(msg)
    }

    fun signup(name: String, number: String, email: String, password: String) {

        //  val context = LocalContext.current

        inProcess.value = true

        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(cutomMessage = "Please Fill All Field")
            return
        }
        inProcess.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signin.value = true
                        createorupdateprofile(name, number, password, email)
                    } else {
                        handleException(it.exception, cutomMessage = "Sign Up Failed")
                        //  Toast.makeText(context,"Sign Up Failed",Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                handleException(cutomMessage = "Number already exists")
                //    Toast.makeText(context,"Number already exists", Toast.LENGTH_SHORT).show()


            }
        }

    }

    fun Loginin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(cutomMessage = "Please fill the all fields")
            return
        } else {
            inProcess.value = true

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signin.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else {
                    handleException(cutomMessage = "Login Failed")

                }
            }
        }
    }

    fun uploadprofileimage(uri: Uri) {
        uploadImage(uri) {
            createorupdateprofile(imageurl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProcess.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            inProcess.value = false
        }.addOnFailureListener {
            handleException(it)
            inProcess.value = false
        }

    }


//    fun createorupdateprofile(
//        name: String? = null,
//        number: String? = null,
//        password: String?=null,
//        email: String?=null,
//        imageurl:String?=null) {
//        val uid = auth.currentUser?.uid
//        val currentUserData = userData.value ?: UserData() // Use a default value if null
//
//        val updatedUserData = UserData(
//            userId = uid,
//            name = name ?: userData.value?.name,
//            number = number ?: userData.value?.number,
//            email=email?:userData.value?.email,
//            password=password?:userData.value?.password,
//            imageurl = imageurl?:userData.value?.imageurl
//        )
//
//        uid?.let {
//         //   inProcess.value = true
//            db.collection(USER_NODE).document(uid).get().addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    // Update data
//                } else {
//                    db.collection(USER_NODE).document(uid).set(updatedUserData)
//                        .addOnSuccessListener {
//                          //  inProcess.value = false
//                            getUserData(uid)
//                        }
//                        .addOnFailureListener { exception ->
//                            handleException(exception, "Cannot Retrieve User")
//                        }
//                }
//            }.addOnFailureListener { exception ->
//                handleException(exception, "Cannot Retrieve User")
//            }
//        }
//    }

    // chat gpt

    fun createorupdateprofile(
        name: String? = null,
        number: String? = null,
        password: String? = null,
        email: String? = null,
        imageurl: String? = null
    ) {
        val uid = auth.currentUser?.uid
        val currentUserData = userData.value ?: UserData() // Use a default value if null

        val updatedUserData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            email = email ?: userData.value?.email,
            password = password ?: userData.value?.password,
            imageurl = imageurl ?: userData.value?.imageurl
        )

        uid?.let {
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Update existing data
                    db.collection(USER_NODE).document(uid).update("imageurl", imageurl)
                        .addOnSuccessListener {
                            // Image URL updated successfully
                            getUserData(uid)
                        }.addOnFailureListener { exception ->
                            handleException(exception, "Cannot Update Image URL")
                        }
                } else {
                    // Create new document
                    db.collection(USER_NODE).document(uid).set(updatedUserData)
                        .addOnSuccessListener {
                            getUserData(uid)
                        }.addOnFailureListener { exception ->
                            handleException(exception, "Cannot Create User Document")
                        }
                }
            }.addOnFailureListener { exception ->
                handleException(exception, "Cannot Retrieve User")
            }
        }
    }


    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Can't retrive user")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
                populateChats()
                populateStatuses()
              //  inProgressStatus.value=false
            }
        }
    }

    fun handleException(exception: Exception? = null, cutomMessage: String = "") {
        Log.e("LiveChatApp", "Live chat exception", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (cutomMessage.isNullOrEmpty()) errorMsg else cutomMessage

        eventMutableState.value = Event(message)
        inProcess.value = false
    }

    fun logout() {
        auth.signOut()
        signin.value = false
        userData.value = null
        DepopulateMessage()
        currentChatMessageListner = null
        eventMutableState.value = Event("Logout")
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(cutomMessage = "Number must be contain Digit Only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),

                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo(
                            "user2.number", number
                        ),

                        )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(cutomMessage = "Number not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageurl,
                                        userData.value?.number
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name, chatPartner.imageurl, chatPartner.number
                                    )

                                )
                                db.collection(CHATS).document(id).set(chat)

                            }
                        }.addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(cutomMessage = "Chat Already Exists")
                }
            }
        }
    }

    fun uploadStatus(uri: Uri) {

        uploadImage(uri) {
            createStatus(it.toString())
        }
    }

    fun createStatus(imageurl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageurl,
                userData.value?.number,
            ),
            imageurl,
            System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(newStatus)
    }

   /* fun populateStatuses() {
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)

            )
        ).addSnapshotListener { value, error ->

            if (error != null) {
                handleException(error)

                if (value != null) {
                    val currentConnection = arrayListOf(userData.value?.userId)
                    val chats = value.toObjects<ChatData>()
                    chats.forEach { chat ->
                        if (chat.user1.userId == userData.value?.userId) {
                            currentConnection.add(chat.user2.userId)
                        } else {
                            currentConnection.add(chat.user1.userId)
                        }
                    }

                    db.collection(STATUS).whereIn("user.userId", currentConnection)
                        .addSnapshotListener { value, error ->
                            if(error!=null){
                                handleException(error)
                            }
                            if(value!=null){
                                status.value=value.toObjects()
                                inProgressStatus.value=false
                            }
                        }
                }
            }

        }
    }

    */

    fun populateStatuses() {
        inProgressStatus.value = true

        // Check if user data is available
        val userId = userData.value?.userId ?: run {
            inProgressStatus.value = false
            return
        }

        val timeDelta=24L *60 *60 *1000
        val cutOff =System.currentTimeMillis()-timeDelta

        // Query CHATS collection for user connections
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId),
                Filter.equalTo("user2.userId", userId)
            )
        ).addSnapshotListener { chatsValue, chatsError ->

            if (chatsError != null) {
                handleException(chatsError)
                inProgressStatus.value = false
                return@addSnapshotListener
            }

            if (chatsValue != null) {
                // Extract user connections
                val currentConnection = arrayListOf(userId)
                val chats = chatsValue.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userId) {
                        chat.user2.userId?.let { currentConnection.add(it) }
                    } else {
                        chat.user1.userId?.let { currentConnection.add(it) }
                    }
                }

                // Query STATUS collection for statuses of user connections
                db.collection(STATUS).whereGreaterThan("timestamp",cutOff).whereIn("user.userId", currentConnection)
                    .addSnapshotListener { statusValue, statusError ->
                        if (statusError != null) {
                            handleException(statusError)
                            inProgressStatus.value = false
                            return@addSnapshotListener
                        }

                        if (statusValue != null) {
                            // Update LiveData with retrieved statuses
                            status.value = statusValue.toObjects()
                            inProgressStatus.value = false
                        }
                    }
            }
        }
    }


}

