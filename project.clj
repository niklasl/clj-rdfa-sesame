(defproject
  rdfa/rdfa-sesame "0.1.0-SNAPSHOT"
  :description "Sesame adapter for the Clojure RDFa library"
  :url "https://github.com/niklasl/clj-rdfa-sesame"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [rdfa/rdfa "0.5.1-SNAPSHOT"]
                 [org.openrdf.sesame/sesame-model "2.6.9"]
                 [org.openrdf.sesame/sesame-rio-api "2.6.9"]]
  :repositories {"aduna-repo" {:url "http://repo.aduna-software.org/maven2/releases/"
                               :snapshots false}},
  :aot [rdfa.adapter.sesame]
  :target-dir "target"
  :jar-exclusions [#"(?:^|/)\..+"])
