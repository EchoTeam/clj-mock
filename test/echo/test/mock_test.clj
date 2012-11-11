(ns echo.test.mock-test
  (:use clojure.test
        echo.test.mock))

(defn func [arg] (is false))

(defmacro has-problem
  [func & body]
  `(let [trigger# (atom false)]
    (with-redefs [~func #(do %& (reset! trigger# true))]
      ~@body
      (is (= true @trigger#)))))

(deftest has-args-valid-function
  (expect [func (has-args [#(boolean %)])]
    (func true)))

(deftest has-args-invalid-function
  (has-problem unexpected-args
    (expect [func (has-args [#(boolean %)])]
      (func false))))

(deftest has-args-valid-value
  (expect [func (has-args [true])]
    (func true)))

(deftest has-args-invalid-value
  (has-problem unexpected-args
    (expect [func (has-args [true])]
      (func false))))

(deftest has-args-valid-several-args
  (expect [func (has-args [true #(not %)])]
    (func true false)))

(deftest has-args-invalid-several-args
  (has-problem unexpected-args
    (expect [func (has-args [true #(not %)])]
      (func false false))))

(deftest returns-without-args
  (expect [func (returns "Test")]
    (is (= "Test" (func)))))

(deftest returns-with-args
  (expect [func (returns "Test")]
    (is (= "Test" (func "arg")))))

(deftest call-without-args
  (expect [func (calls #(str "Test"))]
    (is (= "Test" (func)))))

(deftest call-with-args
  (expect [func (calls #(str "Test" %))]
    (is (= "TestArg" (func "Arg")))))

(deftest correct-times-1
  (expect [func (times 1)]
    (func)))

(deftest correct-times-2
  (expect [func (times once)]
    (func)))

(deftest correct-times-3
  (expect [func (times never)]))

(deftest correct-times-4
  (expect [func (times (more-than 5))]
    (dotimes [_ 6] (func))))

(deftest incorrect-times-1
  (has-problem incorrect-invocation-count
    (expect [func (times 1)])))

(deftest incorrect-times-2
  (has-problem incorrect-invocation-count
    (expect [func (times never)] (func))))

(deftest incorrect-times-3
  (has-problem incorrect-invocation-count
    (expect [func (times (more-than 5))] (func))))

(deftest incorrect-times-3
  (has-problem incorrect-invocation-count
    (expect [func (times (less-than 5))] (dotimes [_ 6] (func)))))

(deftest sucess-complex-1
  (expect [func (->> (has-args [true "test"])
                     (times once)
                     (returns "foobar"))]
    (is (= "foobar" (func true "test")))))

(deftest sucess-complex-2
  (expect [func (->> (has-args [true "test"])
                     (times once)
                     (calls (constantly "foobar")))]
    (is (= "foobar" (func true "test")))))

(deftest failed-complex-1
  (has-problem incorrect-invocation-count
    (expect [func (->> (has-args [true "test"])
                       (times never)
                       (returns "foobar"))]
        (is (= "foobar" (func true "test"))))))

(deftest failed-complex-2
  (has-problem unexpected-args
    (expect [func (->> (has-args [true "test"])
                       (times once)
                       (returns "foobar"))]
        (is (= "foobar" (func false "test"))))))

(deftest google-slurp
  (expect [slurp (->>
                   (has-args [#(re-find #"^http://google.com/" %)])
                   (times once)
                   (returns "success"))]
    (is (= "success" (slurp "http://google.com/test")))))
