package com.routing

import com.core.isValidEmail
import com.dao.ChatGroupDao
import com.dao.UserDao
import com.model.ChatGroupParticipants
import com.model.GroupType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.lang.IllegalArgumentException

private const val BASE = "chatGroup"

fun Application.configureChatGroupRouting(chatGroupDao: ChatGroupDao, userDao: UserDao) {
    routing {

        post("$BASE/createPrivateGroup") {
            try {
                val formParameters = call.receiveParameters()
                val currentUserEmail = formParameters.getOrFail("currentUserEmail")
                val friendEmail = formParameters.getOrFail("friendEmail")

                if (!currentUserEmail.isValidEmail() || !friendEmail.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                if (chatGroupDao.isPrivateGroupExist(currentUserEmail, friendEmail)) {
                    throw IllegalArgumentException("User already a friend")
                }

                val currentUserAccount = userDao.getByEmail(currentUserEmail)
                val friendAccount = userDao.getByEmail(friendEmail)

                if (currentUserAccount != null && friendAccount != null) {
                    val group = chatGroupDao.create(
                        name = "",
                        imageUrl = null,
                        creatorEmail = currentUserEmail,
                        creatorUsername = currentUserAccount.username,
                        creatorProfilePicUrl = currentUserAccount.profilePicUrl,
                        groupType = GroupType.PRIVATE_CHAT_GROUP
                    )

                    if (group != null) {
                        val friend = ChatGroupParticipants(
                            id = group.id,
                            participantEmail = friendEmail,
                            participantUsername = friendAccount.username,
                            participantProfilePicUrl = friendAccount.profilePicUrl
                        )
                        chatGroupDao.addParticipant(
                            groupId = group.id,
                            userName = friendAccount.username,
                            profilePicUrl = friendAccount.profilePicUrl,
                            userEmail = friendEmail
                        )

                        val participants = group.participants.toMutableList()
                        participants.add(friend)

                        call.respond(HttpStatusCode.Created, group.copy(participants = participants))
                    } else call.respond(HttpStatusCode.InternalServerError)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }

        post("$BASE/createGroup") {
            try {
                val formParameters = call.receiveParameters()
                val creatorEmail = formParameters.getOrFail("creatorEmail")
                val name = formParameters.getOrFail("name")
                val imageUrl = formParameters["imageUrl"]
                val creatorUsername = formParameters.getOrFail("creatorUsername")
                val creatorProfilePicUrl = formParameters["creatorProfilePicUrl"]

                if (!creatorEmail.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                if (name.isBlank()) {
                    throw IllegalArgumentException("Name cannot be blank")
                }

                if (chatGroupDao.isChatGroupNameExist(name)) {
                    throw IllegalArgumentException("Group name already used")
                }

                val group = chatGroupDao.create(
                    name = name,
                    imageUrl = imageUrl,
                    creatorEmail = creatorEmail,
                    creatorUsername = creatorUsername,
                    creatorProfilePicUrl = creatorProfilePicUrl,
                    groupType = GroupType.CHAT_GROUP
                )

                if (group == null) {
                    throw java.lang.Exception("Error occurred while creating chat group.")
                }

                call.respond(HttpStatusCode.Created, group)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        put("$BASE/addParticipant") {
            try {
                val formParameters = call.receiveParameters()
                val groupId = formParameters.getOrFail("groupId").toInt()
                val participantEmail = formParameters.getOrFail("participantEmail")

                if (!participantEmail.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val user = userDao.getByEmail(participantEmail)
                if (user == null) {
                    throw IllegalArgumentException("User doesnt exist")
                } else {
                    chatGroupDao.addParticipant(
                        groupId = groupId,
                        userEmail = participantEmail,
                        userName = user.username,
                        profilePicUrl = user.profilePicUrl
                    )
                    call.respond(HttpStatusCode.OK, user)
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        get("$BASE/getGroups/{userEmail}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@get

                if (userEmail.isBlank()) {
                    throw Exception("Email cannot be blank")
                }

                val groups = chatGroupDao.getByEmail(userEmail).map {
                    it.copy(
                        name = if (it.groupType == GroupType.CHAT_GROUP) it.name
                        else it.participants.first { p -> p.participantEmail != userEmail }.participantUsername
                    )
                }

                call.respond(HttpStatusCode.OK, groups)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        put("$BASE/updateGroupImage") {
            try {
                val formParameters = call.receiveParameters()
                val imageUrl = formParameters.getOrFail("imageUrl")
                val groupId = formParameters.getOrFail("groupId").toInt()

                if (imageUrl.isBlank()) {
                    throw IllegalArgumentException("Image url cannot be blank")
                }

                val isUpdated = chatGroupDao.updateGroupImage(groupId, imageUrl)
                if (isUpdated) {
                    call.respond(HttpStatusCode.OK)
                } else call.respond(HttpStatusCode.InternalServerError, "Group image cannot updated")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }
    }
}