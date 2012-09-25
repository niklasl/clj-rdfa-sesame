(ns rdfa.adapter.sesame
  (:import [rdfa.core IRI Literal BNode])
  (:require [rdfa.parser]))


(defn create-node [vf term]
  (condp instance? term
    IRI (.createURI vf (:id term))
    Literal (let [{value :value tag :tag} term]
              (cond
                (instance? IRI tag)
                (.createLiteral vf value (create-node vf tag))
                (not-empty tag)
                (.createLiteral vf value tag)
                :else
                (.createLiteral vf value)))
    BNode (.createBNode vf (:id term))))

(defn triples-to-handler [handler vf triples]
  (doseq [[s p o] triples]
    (let [stmt (.createStatement vf
                                 (create-node vf s)
                                 (create-node vf p)
                                 (create-node vf o))]
      (.handleStatement handler stmt))))

(defn read-to-handler [handler vf & args]
  (.startRDF handler)
  (let [{:keys [env triples proc-triples]} (apply rdfa.parser/get-rdfa args)]
    ; TODO: if proc-triples, check for errors...
    (if-let [vocab (:vocab env)]
      (.handleNamespace handler "" vocab))
    (doseq [[pfx uri] (:prefix-map env)]
      (.handleNamespace handler pfx uri))
    (triples-to-handler handler vf triples))
  (.endRDF handler))


(def rdfa-format
  (org.openrdf.rio.RDFFormat.
    "RDFa" "application/xhtml+xml" nil ".xhtml" true false))


(gen-class
  :name rdfa.adapter.sesame.RDFaParser
  :implements [org.openrdf.rio.RDFParser]
  :state state
  :init init
  :prefix "parser-")

(defn parser-init []
  [[] (atom {})])

(defn parser-parse
  ;parser-read-(InputStream/Reader)-String
  ([this source base]
   (read-to-handler (:handler @(.state this)) (:value-factory @(.state this))
                    source base)))

(defn parser-getRDFFormat [this]
  rdfa-format)

(defn parser-setValueFactory [this valueFactory]
  (swap! (.state this) assoc :value-factory valueFactory))

(defn parser-setRDFHandler [this handler]
  (swap! (.state this) assoc :handler handler))

(defn parser-setParseErrorListener [this err-listener]
  (swap! (.state this) assoc :error-listener err-listener))

(defn parser-setParseLocationListener [this location-listener]
  (swap! (.state this) assoc :location-listener location-listener))

(defn parser-setParserConfig [this config]
  (swap! (.state this) assoc :config config))

(defn parser-getParserConfig [this]
  (:config (.state this)))

(defn parser-setDatatypeHandling [this datatypeHandling]
  (swap! (.state this) assoc :datatype-handling datatypeHandling))

(defn parser-setPreserveBNodeIDs [this preserveBNodeIDs]
  (swap! (.state this) assoc :preserve-bnode-ids preserveBNodeIDs))

(defn parser-setStopAtFirstError [this stopAtFirstError]
  (swap! (.state this) assoc :stop-at-first-error stopAtFirstError))

(defn parser-setVerifyData [this verifyData]
  (swap! (.state this) assoc :verify-data verifyData))


(gen-class :name rdfa.adapter.sesame.RDFaParserFactory
  :implements [org.openrdf.rio.RDFParserFactory]
  :prefix "factory-")

(defn factory-getParser [this] (rdfa.adapter.sesame.RDFaParser.))

(defn factory-getRDFFormat [this] rdfa-format)

