# FLAW 1:
- Cross-Site Scripting (XSS), A7:2017

### Description:
Username is listed on index page as unescaped text. This makes it possible to inject javascript.

### How to reproduce:
1. Write to Create New: Username: "a<script>{javascript}</script>", for example: "a<script>alert(document.cookie)</script>"
2. Enter any password.
3. Click Create.
4. Now when any user views the user page, it'll run the javascript written.

### How to fix it:
Make the username listed as escaped text, by changing the value from th:utext to th:text.


# FLAW 2:
- Sensitive Data Exposure, A3:2017
- Broken Access Control, A5:2017

### Description:
By going to user profile page, we can see from the URL that the profile page is /account/{id}, where {id} is the current user accout id. By changing this, we can see other people information. However, we cannot edit the information, as it requires the user password.
One could attempt to brute force the password though.

### How to reproduce:
1. Login with any user (create new one if need be).
2. Go to url localhost:8080/account/{id}, where {id} is the user id number. For example, 1 is admin id.

### How to fix it:
One possible fix is, that make profile page show current user information on /account. And for admin, viewing other people profiles could happen from /account/{id}, and this needs a check that makes sure the current user is admin.


# FLAW 3:
- Broken Access Control, A5:2017

### Description:
It's possible to remove users without being admin.
By being only logged in, it's possible to remove other users by sending post request to /remove with parameter id={id}, where {id} is a user id. This requires that a user is authenticated (has a valid JSESSION cookie).

### How to reproduce:
1. Login with any user to get sessionid cookie.
2. Send post request to address "localhost:8080/remove", with Cookie: "JSESSIONID={cookie value}" in header and "id={id}" in body, where {id} is the user id and {cookie value} is the session id.

### How to fix it:
And to fix removing user by "normal" users, make authentication check which makes sure the logged in user is an admin.


# FLAW 4:
- Broken Authentication, A2:2017

### Description:
Admin password is easy to fuzz / brute force.
Logging in, we see list of all users. There is "admin" name.
Or by looking at the source code: config/CustomUserDetails, method init(), we see that admin login is created with a password.
Or maybe we can guess that admin is a valid username.
However, password is encrypted, so we cannot know what it is.
Fuzzing loging with wordlist 10-million-password-list-top-10000.txt or rockyou.txt or maybe some other wordlist will find the correct password.

### How to reproduce:
1. Use any program that is capable of sending requests and alter the post body.
2. Send post requests with parameters "username=admin&password={pw}", where {pw} is a changing password. Use rockyou wordlist. Note: if you FUZZ, you will not get response body back, but do get header. Atleast in ZAP.
3. Search response headers. With correct password, you get Location: "http://localhost:8080/", invalid gets "http://localhost:8080/login?error". Also, with valid password, cookie gets set: "Set-Cookie: {cookie}".
4. Look at the request, there you should see the valid password.

### How to fix it:
Admin should use more secure password.
Brute force blocking should be included. Blocking attempts by ip is a good way to implement blocking.
This is done by taking note how many times an ip fails to login with a username. After certain amount of logins, the ip is blocked for the username. A successfull login clears the failed login count.
-- This is included in the program, but commented out in AuthenticationFailureEventListener. Remove the comments and we're good to go.


# FLAW 5:
- Security Misconfiguration, A6:2017
- Broken Authentication, A2:2017

### Description:
Once server is launched cookie JSESSION gets a random long number, all following cookies get the value of previous JSESSION +1. Here we can check previous sessions and see if someone is logged in. As we get their session, we can now "pretend" to be them without logging in.

### How to reproduce:
1. Login with any account.
2. Change cookie JSESSIONID to one lower number.

### How to fix it:
Use Springs default configuration, that generates random JSESSION id's. Or random them yourself.
Here we can remove the customCookieFactory class.


# FLAW 6:
- Broken Authentication, A2:2017

### Description:
Admin username and password is hard coded into the source code. Source is available from internet for everyone.

### How to fix it:
If we want to have predetermined admin account and password for it, those should be read from a database or an environment file. Any passwords and usernames put into source code is not good, especially when the source code is available for everyone to get.

