function loginDouban() {

    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
//    alert(email+password);
    LoginServiceForJs.loginDouban(email, password);

}