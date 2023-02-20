(ns startrek.shared.middleware.ui-exceptions
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]
   [startrek.core.aux.thymeleaf.interface :as thymeleaf]
   [startrek.ui.utils.response :as response-utils]))

(set! *warn-on-reflection* true)

(defn ^:private error
  [request {:keys [status body] :as response}]
  (let [hx-request? (get-in request [:headers "hx-request"])]
    (-> (thymeleaf/render (format "/shared/exceptions/error-%s" (if hx-request? "section" "page")) body request)
        (response-utils/html)
        (response/status status))))

(defn ^:private with-ui-exceptions
  [{:keys [tag] :as route-data} _]
  (when (= :ui tag)
    (fn [handler]
      (fn [request]
        (let [{:keys [status] :as response} (handler request)]
          (if (< 400 status) (error request response) response))))))

(def ui-exceptions-middleware
  {:name ::ui-exceptions
   :description "Adds UI exception handling."
   :compile with-ui-exceptions})
