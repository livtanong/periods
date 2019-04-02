.PHONY:
	echo "hi"

test:
	clojure -Atest-clj

jar:
	clojure -Ajar

release-patch:
	clojure -Agaramond patch --tag --pom

release-minor:
	clojure -Agaramond minor --tag --pom

release-major:
	clojure -Agaramond major --tag --pom
