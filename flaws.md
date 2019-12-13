# FLAW 1:
### XSS injection

##### Description:
Username is listed on index page as unescaped text. This makes it possible to inject javascript.

##### How to fix it:
Make the username listed as escaped text, by changing the value from th:utext to th:text.


# FLAW 2:
### URL tampering

##### Description:
By going to user profile page, we can see from the URL that the profile page is /account/{id}, where {id} is the current user accout id. By changing this, we can see other people information. However, we cannot edit the information, as it requires the user password.

One could attempt to brute force the password though.

##### How to fix it:
One possible fix is, that make profile page show current user information on /account. And for admin, viewing other people profiles could happen from /account/{id}, and this needs a check that makes sure the current user is admin.


# FLAW 3:
### CSRF vulnerability

##### Description:
CSRF token is not set. By knowing the session cookie, one can send post requests to site.

For example, it's possible to remove other users by sending post request to /remove with parameter id={id}, where {id} is a user id. This requires that a user is authenticated (has valid JSESSION cookie).

##### How to fix it:
Enable CSRF protection.

And to fix removing user by "normal" users, make authentication check which makes sure the logged in user is an admin.


# FLAW 4:
### Admin password is easy to fuzz / brute force

##### Description:
Logging in, we see list of all users. There is "admin" name. Or by looking at the source code: config/CustomUserDetails, method init(), we see that admin login is created with a password. Or maybe we can guess that admin is a valid username.

However, password is encrypted, so we cannot know what it is.

Fuzzing loging with wordlist 10-million-password-list-top-10000.txt or rockyou.txt or maybe some other wordlist will find the correct password.

##### How to fix it:
- Admin should use more secure password.
- Brute force blocking should be included. Blocking attempts by ip is a good way to implement blocking.
- This is done by taking note how many times an ip fails to login with a username. After certain amount of logins, the ip is blocked for the username. A successfull login clears the failed login count.
- Fix is included in the program, but commented out in AuthenticationFailureEventListener. Remove the comments and we're good to go.


# FLAW 5:
### SessionID is easy to guess

##### Description:
Once server is launched cookie JSESSION gets a random long number, all following cookies get the value of previous JSESSION +1. Here we can check previous sessions and see if someone is logged in. As we get their session, we can now "pretend" to be them without logging in.

##### How to fix it:
Use Springs default configuration, that generates random JSESSION id's. Or random them yourself.

Here we can remove the customCookieFactory class.


# FLAW 6 (extra):
### Admin is hard coded into src. Src is in github.

##### Description:
Admin username and password is hard coded into the source code. Source is available from internet for everyone.

##### How to fix it:
If we want to have predetermined admin account and password for it, those should be read from a database or an environment file. Any passwords and usernames put into source code is not good, especially when the source code is available for everyone to get.
