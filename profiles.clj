{:user               {:plugins                  [[lein-pprint "1.1.2"]
                                                 [lein-subscribable-urls "0.1.0-alpha2"]
                                                 [lein-lein "0.2.0"]
                                                 [lein-jdk-tools "0.1.1"]
                                                 [threatgrid/trim-sl4j-classpath "0.1.0"]]
                      :jvm-opts                 [;; Remove useless icon from the Dock:
                                                 "-Dapple.awt.UIElement=true"
                                                 ;; Make more info available to debuggers:
                                                 "-Dclojure.compiler.disable-locals-clearing=true"
                                                 ;; If failing on startup, print stacktraces directly instead of saving them to a file:
                                                 "-Dclojure.main.report=stderr"
                                                 ;; Enable tiered compilation, for guaranteeing accurate benchmarking (at the cost of slower startup):
                                                 "-XX:+TieredCompilation"
                                                 ;; Don't elide stacktraces:
                                                 "-XX:-OmitStackTraceInFastThrow"
                                                 ;; Prevents a specific type of OOMs:
                                                 "-XX:CompressedClassSpaceSize=3G"
                                                 ;; Prevents trivial StackOverflow errors:
                                                 "-XX:MaxJavaStackTraceDepth=1000000"
                                                 ;; Set a generous limit as the maximum that can be allocated, preventing certain types of OOMs:
                                                 "-Xmx18G"
                                                 ;; increase stack size x6, for preventing SO errors:
                                                 ;;   (The current default can be found with
                                                 ;;    `java -XX:+PrintFlagsFinal -version 2>/dev/null | grep "intx ThreadStackSize"`)
                                                 "-Xss6144k"
                                                 ;; Improves startup time:
                                                 "-Xverify:none"
                                                 ;; Enable various optimizations, for guaranteeing accurate benchmarking (at the cost of slower startup):
                                                 "-server"]
                      :monkeypatch-clojure-test false}

 ;; The following flags setup GC with short STW pauses, which tend to be apt for webserver workloads.
 ;; Taken from https://docs.oracle.com/cd/E40972_01/doc.70/e40973/cnf_jvmgc.htm#autoId2
 :gcg1               {:jvm-opts ["-XX:+UseG1GC"
                                 "-XX:MaxGCPauseMillis=200"
                                 "-XX:ParallelGCThreads=20"
                                 "-XX:ConcGCThreads=5"
                                 "-XX:InitiatingHeapOccupancyPercent=70"]}

 ;; Throws if core.async blocking ops (>!!, <!!, alts!!, alt!!) are used in a go block
 ;; (added in a separate profile since some apps break on it)
 :async-checking     {:jvm-opts ["-Dclojure.core.async.go-checking=true"]}

 ;; remember to keep this in sync with exports.sh
 :yourkit
 {:jvm-opts
  ["-agentpath:/Applications/YourKit-Java-Profiler-2019.8.app/Contents/Resources/bin/mac/libyjpagent.dylib=quiet,sessionname={YOURKIT_SESSION_NAME}"]}

 :repl               {:middleware                        [leiningen.resolve-java-sources-and-javadocs/middleware
                                                          leiningen.trim-sl4j-classpath/middleware]
                      :plugins                           [[threatgrid/resolve-java-sources-and-javadocs "1.3.0"]]
                      :resolve-java-sources-and-javadocs {:classifiers #{"sources"}}
                      :jvm-opts                          ["-Dleiningen.resolve-java-sources-and-javadocs.throw=true"]}

 :clojars            {:deploy-repositories [["clojars"
                                             {:url           "https://clojars.org/repo/",
                                              :sign-releases false}]]}

 ;; the following profile serves for two use cases:
 ;; * Launching `lein repl` from iTerm
 ;; * Launching an in-Emacs JVM
 ;; Perhaps for the latter, the :plugins section is redundant. Hasn't given problems so far.
 :emacs-backend      {:dependencies   [[cider/cider-nrepl "0.16.0"]
                                       [clj-stacktrace "0.2.8"]
                                       [com.clojure-goes-fast/clj-java-decompiler "0.2.1"]
                                       [com.nedap.staffing-solutions/utils.collections "2.1.0"]
                                       [com.stuartsierra/component.repl "0.2.0"]
                                       [criterium "0.4.5"]
                                       [clj-kondo "2021.01.20"]
                                       [formatting-stack "4.3.0-alpha1"]
                                       [lambdaisland/deep-diff "0.0-29"]
                                       [medley "1.2.0"]
                                       [nrepl-debugger "0.1.0-SNAPSHOT"]
                                       [org.clojure/clojure "1.10.1"]
                                       [org.clojure/math.combinatorics "0.1.6"]
                                       [org.clojure/test.check "1.1.0"]
                                       [org.clojure/java.jmx "1.0.0"]
                                       [org.clojure/spec.alpha "0.2.194"]
                                       [org.clojure/tools.namespace "1.1.0"]
                                       [org.clojure/tools.nrepl "0.2.13"]
                                       [org.clojure/tools.reader "1.3.3"]
                                       [threatgrid/formatting-stack.are-linter "0.1.0-alpha1"]
                                       ;; Ensure Jackson is consistent and up-to-date:
                                       [com.fasterxml.jackson.core/jackson-annotations "2.11.2"]
                                       [com.fasterxml.jackson.core/jackson-core "2.11.2"]
                                       [com.fasterxml.jackson.core/jackson-databind "2.11.2"]
                                       [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor "2.11.2"]
                                       [com.fasterxml.jackson.datatype/jackson-datatype-jsr310 "2.11.2"]
                                       [com.fasterxml.jackson.dataformat/jackson-dataformat-smile "2.11.2"]]

                      :source-paths   ["/Users/vemv/.lein/scripts"]

                      :jvm-opts       ["-Dformatting-stack.eastwood.parallelize-linters=true"]

                      :resource-paths [;; http://rebl.cognitect.com/download.html
                                       "/Users/vemv/.lein/resources/rebl.jar"]

                      :plugins        [[refactor-nrepl "2.4.0" :exclusions [org.clojure/tools.logging]]
                                       [cider/cider-nrepl "0.16.0"]]

                      :repl-options   {:port    41235
                                       :timeout 900000
                                       :welcome "Print nothing"
                                       :init    {:emacs-backend (clojure.core/require 'vemv.emacs-backend)}}}

 :emacs-backend-init {:repl-options {:init {:emacs-backend-init (clojure.core/require 'vemv.anyrefresh)}}}

 :iroh-global        {:dependencies      [[threatgrid/trapperkeeper "3.1.0"]
                                          [threatgrid/trapperkeeper-webserver-jetty9 "4.2.0"]]
                      :source-paths      [#_ "/Users/vemv/trapperkeeper-webserver-jetty9/test/clj"
                                          "/Users/vemv/formatting-stack.alias-rewriter/src"
                                          ;; `lein with-profile -user cljx once`:
                                          "/Users/vemv/schema/target/generated/src/clj"]
                      :java-source-paths [#_ "/Users/vemv/trapperkeeper-webserver-jetty9/test/java"]
                      :jvm-opts          ["-Diroh.test.dotests.elide-explanations=true"
                                          "-Diroh.dev.logging.level=:error"
                                          "-Diroh.dev.logging.enable-println-appender=false"
                                          ;; "-Diroh.enable-response-profiling=true"
                                          "-Diroh.dev.logging.enable-file-appender=true"
                                          "-Diroh.dev.logging.order-chronologically=false"]}

 :parallel-reload    {:dependencies [[threatgrid/parallel-reload "0.2.2"]
                                     [commons-io/commons-io "2.8.0"] ;; for the Tailer class
                                     [org.clojure/clojure "1.11.99"]]

                      :jvm-opts     ["-Djava.awt.headless=false" ;; ensure the clipboard is usable
                                     #_ "-Dcisco.tools.namespace.parallel-refresh.debug=true"
                                     ;; experiment - try triggering GC more frequently:
                                     ;; (didn't work originally, but it might after the SoftRef hack)
                                     ;; "-XX:MaxMetaspaceExpansion=0"
                                     ]
                      :aliases      {"nrepl" ["run" "-m" "vemv.nrepl"]}
                      :repositories [["https://packagecloud.io/vemv/clojure/maven2"
                                      {:url "https://packagecloud.io/vemv/clojure/maven2"}]]}

 ;; for hacking on refactor-nrepl itself
 :refactor-nrepl     {:dependencies [[http-kit "2.3.0"]
                                     [cheshire "5.8.0"]
                                     [org.clojure/tools.analyzer.jvm "0.7.1"]
                                     [org.clojure/tools.namespace "0.3.0-alpha3"
                                      :exclusions [org.clojure/tools.reader]]
                                     [org.clojure/tools.reader "1.1.1"]
                                     [cider/orchard "0.3.0"]
                                     [cljfmt "0.6.3"]
                                     [me.raynes/fs "1.4.6"]
                                     [rewrite-clj "0.6.0"]
                                     [cljs-tooling "0.2.0"]
                                     [version-clj "0.1.2"]]}

 :emacs-figwheel     {:dependencies [[com.cemerick/piggieback "0.2.2"]
                                     [figwheel-sidecar "0.5.16"]]
                      :plugins      [[cider/cider-nrepl "0.16.0"]]
                      :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                      :figwheel     {:nrepl-middleware ["cider.nrepl/cider-middleware"
                                                        "refactor-nrepl.middleware/wrap-refactor"
                                                        "cemerick.piggieback/wrap-cljs-repl"]}}

 :eftest             {:plugins [[lein-eftest "0.5.8"]]
                      :eftest  {:multithread? false
                                :fail-fast?   true}}}
