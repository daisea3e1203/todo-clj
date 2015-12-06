(ns todo-clj.core
  (:require [compojure.core :refer [defroutes context GET]]
    [compojure.route :as route]
    [ring.adapter.jetty :as server]
    [ring.util.response :as res]))

(defonce server (atom nil))

(defn html [res]
  (res/content-type res "text/html; charset=utf-8"))

(defn home-view [req]
  "<h1>ホーム画面</h1>
   <a href=\"/todo\">TODO 一覧</a>")

(defn home [req]
  (-> (home-view req)
      res/response
      html))

(def todo-list
  [{:title "朝ご飯"}
   {:title "卵"}
   {:title "ゴミ出し"}
   {:title "大漁"}])

(defn todo-index-view [req]
  `("<h1>TODO 一覧</h1>"
    "<ul>"
    ~@(for [{:keys [title]} todo-list]
        (str "<li>" title "</li>"))
        "</ul>"))

(defn todo-index [req]
  (-> (todo-index-view req)
      res/response
      html))

(def routes
  {"/" home
   "/todo" todo-index})

; (defn handler [req]
;   {:status 200
;   :headers {"Content-Type" "text/plain"}
;   :body "Hello, world!!"})

(defroutes handler
  (GET "/" req home)
  (GET "/todo" req todo-index)
  (route/not-found "<h1>404 page not found</h1>"))

; (defn handler [req]
;   (let [uri (:uri req)
;     maybe-fn (match-route uri)]
;     (if maybe-fn
;       (maybe-fn req)
;       (not-found))))

(defn start-server []
  (when-not @server
    (reset! server (server/run-jetty #'handler {:port 3000 :join? false}))))

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))
