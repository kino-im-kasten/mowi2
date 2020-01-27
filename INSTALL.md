# Installation

## Disclaimer

This file gives the reader a detailed guide on how to setup and install this project. Event though the examples are provided __for Linux (Debian)__, the process is explained in general. If your system differs from Debian Linux, __you will need to port these steps to your desired system of choice__.

## Setup

We assume you set up an operating system with a privileged user. You will then need to follow these steps in this specific order to get the software up and running.

### Packages

You will need to install a few packages at first. These are:

* MariaDB and MySQL
* Java 11 (OpenJDK 11) or the default for your system
* Net-Tools

``` BASH
# For Debian 10, the packages will do the job
sudo apt-get -y install mariadb-server net-tools wget  openjdk-11-*

# If you would rather like to install the default jdk, install
sudo apt-get -y install default-jdk
```

### Setting up _MariaDB_

We will need to setup _MariaDB_, so that we will be able to use it with our spring application. **Make sure**, to not have any other SQL instances open and listening. This will break Spring. If you want look up, whether MySQL is there and listening, use `netstat -tul | grep mysql`.

### Starting _MariaDB_

Start the _MariaDB_ via systemctl start mariadb.service

#### Secure installation

We will start of with creating a secure installation.

``` TXT
[user@host] : [ ~ ] $ sudo mysql_secure_installation
NOTE: RUNNING ALL PARTS OF THIS SCRIPT IS RECOMMENDED FOR ALL MariaDB
      SERVERS IN PRODUCTION USE!  PLEASE READ EACH STEP CAREFULLY!

In order to log into MariaDB to secure it, we will need the current
password for the root user.  If you have just installed MariaDB, and
you have not set the root password yet, the password will be blank,
so you should just press enter here.

Enter current password for root (enter for none): [[ENTER]]
OK, successfully used password, moving on...

Setting the root password ensures that nobody can log into the MariaDB
root user without the proper authorisation.

Set root password? [Y/n] Y
New password: swtswt
Re-enter new password: swtswt
Password updated successfully!
Reloading privilege tables..
 ... Success!


By default, a MariaDB installation has an anonymous user, allowing anyone
to log into MariaDB without having to have a user account created for
them.  This is intended only for testing, and to make the installation
go a bit smoother.  You should remove them before moving into a
production environment.

Remove anonymous users? [Y/n] Y
 ... Success!

Normally, root should only be allowed to connect from 'localhost'.  This
ensures that someone cannot guess at the root password from the network.

Disallow root login remotely? [Y/n] n
 ... Success!

By default, MariaDB comes with a database named 'test' that anyone can
access.  This is also intended only for testing, and should be removed
before moving into a production environment.

Remove test database and access to it? [Y/n] Y
 - Dropping test database...
 ... Success!
 - Removing privileges on test database...
 ... Success!

Reloading the privilege tables will ensure that all changes made so far
will take effect immediately.

Reload privilege tables now? [Y/n] Y
 ... Success!

Cleaning up...

All done!  If you've completed all of the above steps, your MariaDB
installation should now be secure.

Thanks for using MariaDB!

```

For further information, consult [rootusers.com](https://www.rootusers.com/how-to-install-and-configure-mariadb/).

#### Changing some settings

[//]: # (https://stackoverflow.com/questions/43379892/mariadb-cannot-login-as-root)

We will then need to change a few settings, namely the authentication method, to not use the unix-socket. **If the next command does not work, try it with `sudo` and without the `-p` flag.**

``` BASH
mariadb -u root -p
Enter password: swtswt
```

You are then loggin in into _MariaDB_. Use the following command to look up what auth-method is being used. If

``` SQL
SELECT user, host, plugin FROM mysql.user;
```

yields something like

``` TXT
+------+-----------+-------------+
| user | host      | plugin      |
+------+-----------+-------------+
| root | localhost | unix_socket |
+------+-----------+-------------+
```

you will need change it. Use

``` SQL
UPDATE mysql.user SET plugin = '' WHERE plugin = 'unix_socket';
FLUSH PRIVILEGES;
```

Then, it should look like this:

``` TXT
+------+-----------+--------+
| user | host      | plugin |
+------+-----------+--------+
| root | localhost |        |
+------+-----------+--------+
```

#### Changing password if necessary

[//]: # (To manage users, <https://mariadb.com/kb/en/create-user/>)

If you would like to change the password later on, use

``` SQL
SET PASSWORD FOR root@localhost = PASSWORD('swtswt');
```

The password the spring configuration is using is located in the `application.properties` file, and is by default set to `swtswt`.

#### Creating the database

Last but not least, we will create the necessary database.

``` SQL
DROP DATABASE IF EXISTS swt;
CREATE DATABASE swt;
```

We have now set up _MariaDB_.

### Setting up _Maven (2)_

We use _Apache Maven_ for our build dependencies. For a detailed guide on how to install maven, use [this site](https://www.howtoforge.com/tutorial/how-to-install-apache-maven-on-debian-10/) or the search engine of you choice.

We will then need to setup the `JAVA_HOME` environment variable that _Maven_ needs.

``` BASH
JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
echo "export JAVA_HOME=${JAVA_HOME}" | sudo tee -a /etc/profile
```

### The Application itself

Copy the application files onto the syste. The `.tar.gz` or `.7z` file will need to be extracted. You can use `tar` or `7z` to accomplish this.

``` BASH
# for tar, use
tar -xvzf swt19w4.tar.gz

# for 7zip, use
7z x swt19w4.7z
```

We will then `cd` into the directory and execute _Maven_ to build the project. Afterwards, we will execute the `jar`. If you would like to make changes to `application.properties`, in case your database setup is different or you would like to use other users, use the first command.

``` BASH
cd swt19w4

# to change some settings, head here
vi src/main/resources/application.properties

# to start Maven, use
./mvnw clean package -DskipTests
#bei Fehler wegen fehlenden .mvn Order: mvn clean package -DskipTests

# to execute the built jar
nohup java -jar target/swt19w4-1.0.0.BUILD-SNAPSHOT.jar &

# to read the log, use
tail -f nohub.out
```

That's it. The application should be up and running. There are a few warnings that you can ignore during startup.

### Cleanup and restart

When the application has run, you could (it is highly recommended) shut it down again and change a property in the `application.properties` file. You should change

``` TXT
spring.jpa.hibernate.ddl-auto=create
```

to

``` TXT
spring.jpa.hibernate.ddl-auto=validate
```

because the database-scheme has already been created. Validation is for production workflows.
