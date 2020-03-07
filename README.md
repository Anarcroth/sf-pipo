# SFPIPO

Simple File Ping-Pong: a web server used to ping-pong a couple of files.

---

Problem to solve: I often get into a situation where I need to send myself some files and none of the technologies I have around me really do what I want them to do. They are either too clunky, or not fast enough, or would get backed up with a lot of redundant junk. Can't use Slack or Skype, because I will just backup all of my history with files and it will get confusing. Cant' use Google Drive, because Google is evil. If I use a flash drive, I'll just forget it somewhere. Can't setup a home server, because the damn ISP doesn't want to cooperate with me.

What I really needed is some remote storage, where I can upload/download files, the ones I need to share with myself, in an easy and fast fashion. This is the real purpose of SFPIPO - to ping-pong myself files on a day-to-day basis.

---

### Running

**Remote**

It's very simple to setup this project for yourself. All you need is to either have a hosting provider that can run Clojure (such as Heroku), or have your own server where you can deploy the app for yourself.

Heroku has a [simple tutorial](https://devcenter.heroku.com/articles/getting-started-with-clojure) on how to deploy your app to them.

**Localhost (or self-host)**

To run the app locally is as simple as

``` bash
# clone project
git clone https://github.com/Anarcroth/sfpipo.git

# move to project dir
cd sfpipo

# run with leiningen
lein run
```

Alternatively, you can build yourself an uberjar and run it with Java.

``` bash
# create uberjar
lein uberjar

# run
java $JVM_OPTS -cp target/sfpipo.jar clojure.main -m sfpipo.core
```

### Usage

Using sfpipo is very easy. Just throw `curl` at it. This allows you to quickly and painlessly upload/get files from your remote. No GUI, no clicking, no nothing.

Currently, the server supports:

* Getting files.
``` bash
# Get file from server
cur -X -GET "https://<your address>/file/<filename>" -o <filename>
```

* Uploading files.
``` bash
# Upload file to server
cur -X -POST "https://<your address>/upload" -F file=@<yourfile>
```

* Deleting files.
``` bash
# Delete file from server
cur -X -DELETE "https://<your address>/file/<filename>"
```

* Listing uploaded files.
``` bash
# List uploaded files to server
cur -X -GET "https://<your address>/list-files"
```

* Throwing errors like a boss. (has to be fixed)

### Security

SFPIPO **is not secure**. If you upload something over `http` or upload a file that has sensitive data, **you should be responsible for managing the safety of that data**.

If you want to upload data that is secured, then encrypt the data beforehand with `gpg` or something else. If you upload encrypted information, even if it gets stolen, [cracking gpg](https://security.stackexchange.com/questions/77340/how-easy-is-it-to-crack-gpg-with-private-key-but-without-password) is a challenge on its own, so don't worry it will get compromised.

**WARNING NOTE:** If you use a remote service, like Heroku, then people who know the address of your app **can** get your files. The risks are smaller for this if you have a self-host, but keep in mind that **currently**, there is no authentication on sfpipo.

### License

Copyright © 2020 Martin Nestorov

Author: Martin Nestorov mnestorov@protonmail.com

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
