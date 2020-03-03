(defproject sf-pipo "0.1.0-SNAPSHOT"
  :description "Simple File Ping-Pong: a web server used to ping-pong a couple of files."
  :url "http://example.com/FIXME"
  :license {:name "GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.0"]
                 [compojure "1.6.1"]]
  :repl-options {:init-ns sf-pipo.core}
  :main sf-pipo.core
  :profiles {:dev
             {:main sf-pipo.core/-dev-main}})
