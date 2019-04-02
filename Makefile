.PHONY:
	echo "hi"

test:
	clojure -Atest-clj

jar:
	clojure -Ajar

release-patch:
	clojure -Agaramond patch --tag --pom
	clojure -Ajar

release-minor:
	clojure -Agaramond minor --tag --pom
	clojure -Ajar

release-major:
	clojure -Agaramond major --tag --pom
	clojure -Ajar
