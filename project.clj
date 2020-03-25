(defproject sf-pipo "0.1.0-SNAPSHOT"
  :description "Simple File Ping-Pong: a web server used to ping-pong a couple of files."
  :url "https://github.com/Anarcroth/sfpipo"
  :license {:name "GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.0.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ring "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.1"]
                 [environ "1.1.0"]
                 [buddy/buddy-auth "2.2.0"]]
  :min-lein-version "2.0.0"
  :uberjar-name "sfpipo.jar"
  :main ^:skip-aot sfpipo.core
  :profiles {:dev {:main sfpipo.core/-dev-main
                   :plugins [[lein-environ "1.0.0"]]
                   :env {:squiggly {:checkers [:eastwood :kibit :typed]}}}
             :uberjar {:aot :all
                       :env {:production true}}})
