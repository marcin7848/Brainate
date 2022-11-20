# Brainate

WEB Application dedicated to languages learning. Application was built using Java 11, Angular Framework 8, Node JS 10, Spring Boot 2 and Google Cloud Translation v3.

## Requirements

 - Java 11
 - Angular Framework 8
 - Node JS 10
 - Gradle 6

## Build

Before building, edit resources and set up application.

- Create Postgres database
- Open /src/main/resources/application.properties and edit connection data to your database
- **HTTPS** support - application is configured to use SSL Certificate. It allows you to use some features like:
1. Auto reading texts in specified langauge (e.g. british or american english, german etc. All available languages are listed [here - Google Text-To-Speech](https://cloud.google.com/text-to-speech/docs/voices))
2. Enter texts by speaking - [Gloogle Speech-To-Text](https://cloud.google.com/speech-to-text)

<details>
<summary>If you DO NOT have a SSL certificate and you want to remove HTTPS support:</summary>
1. Open /src/client/src/app/account-management/account.service.ts
Find:
<blockquote>/oauth/token</blockquote>
Replace by:
<blockquote>//localhost:80/oauth/token</blockquote>
2. /src/client/src/app/app.inceptor.ts
Find 2x:
<blockquote>/oauth/token</blockquote>
Replace 2x by:
<blockquote>//localhost:80/oauth/token</blockquote>
3. /src/client/proxy.conf.json
Find:
<blockquote>https://localhost:443/</blockquote>
Replace by:
<blockquote>http://localhost/</blockquote>
Find:
<blockquote>"secure": true</blockquote>
Replace by:
<blockquote>"secure": false</blockquote>
4. /src/main/resources/application.properties
Uncomment:
<blockquote>server.port=80</blockquote>
Comment / remove everything below
5. /src/main/java/com/brainate/config/RootConfig.java
Comment / remove 2 methods: servletContainer and redirectConnector
</details><br />If you want to use HTTPS - remember to update /src/main/resources/application.properties [server.ssl.]

##### Features
- Follow  [Gloogle Cloud Translation documentation](https://cloud.google.com/translate/docs). Generate Google_Translate.json and set the environment variable:
`export GOOGLE_APPLICATION_CREDENTIALS="/home/user/Downloads/Google_Translate.json"`
- SSL: Generate keystore.p12 using e.g. openssl:
`openssl pkcs12 -export -in certificate.crt -inkey private.key -certfile ca_bundle.crt -out keystore.p12 -name "brainate" -passin pass:pass123 -passout pass:pass123`
Use same data as provided in /src/main/resources/application.properties.

If you want to generate keystore.p12 automatically copy your certificate.crt and private.key to /docker_openssl/certificate/ (+ intermediate cartificates in ca_bundle.crt - remember to add to Dockerfile -certfile ca_bundle.crt) and use:
> docker-compose up

Copy /docker_openssl/certificate/keystore.p12 to the same path as jar file.

- Build application using:
> gradle build

You can use docker to build application.  Copy files from main directory to /docker_brainateBuild/app/ and use:
> docker-compose up

Copy /build/libs/Brainate.jar to your server.
+Copy Google_Translation.json to the same path as jar file (if you want to use auto translations)
+Copy keystore.p12 to the same path as jar file (if you want to use HTTPS and features)

Run applicartion:
> java -Xmx2048M -jar Brainate.jar


## Useful
<details>
<summary>CRON Task - Auto update daily quests</summary>
1. Install cron
2. Open cron tasks:
<blockquote>crontab -e</blockquote>
3. Add at the end:
<blockquote>0 4 * * * curl -s https://localhost/api/languages/reloadTasks > /dev/null</blockquote>
</details>

<details>
<summary>Auto database dump</summary>
1. Create new folder: `mkdir /brainateDatabase`
2. Change priviliges: `chmod 777 brainateDatabase`
3. Create file /brainateDump.sh
4. Enter bash script:
<blockquote>
#!/bin/bash

dateTime=$(date +"%Y-%m-%d_%T")
sudo su - postgres -c  "pg_dump brainate >> /brainateDatabase/brainate_$dateTime.sql"
</blockquote>
5. Modify priviliges: `chmod u+x brainateDump.sh`
6. Cron job: `0 5 * * * /brainateDump.sh`
</details>