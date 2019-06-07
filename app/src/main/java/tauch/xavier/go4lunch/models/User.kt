package tauch.xavier.go4lunch.models

data class User (
    var userId: String = "",
    var username: String? = "",
    var userEmail: String? = "",
    var userPicture: String? = "",
    var userRestaurantChoice: String = "",
    var userHasPicked: Boolean = false

    )