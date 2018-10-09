package onedaycat.com.foodfantasyservicelib.util.idgen

import java.util.*

class IdGen {
    companion object {
        var freezeId = ""

        // Freeze id when call new
        fun setFreezeID(id: String) {
            freezeId = id
        }

        //Reset freeze stop freeze id
        fun resetFreeze() {
            freezeId = ""
        }

        //New id
        fun NewId(): String {
            if (freezeId == "") {
                return UUID.randomUUID().toString()
            }

            return freezeId
        }
    }
}