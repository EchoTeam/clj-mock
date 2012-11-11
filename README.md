# mock

Extract clojure.contrib.mock as separate library

##Usage

[Mock API](http://richhickey.github.com/clojure-contrib/mock-api.html)

```clojure
(expect [slurp (->>
                 (has-args [#(re-find #"^http://google.com/" %)])
                 (times once)
                 (returns "success"))]
  (is (= "success" (slurp "http://google.com/test")))))
```

## Installation

Depend on `[org.clojars.echo/test.mock "0.1.2"]` in your `project.clj`

## License

Distributed under the Eclipse Public License, the same as Clojure.
