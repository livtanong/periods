.PHONY:
	echo "hi"

test:
	clojure -Atest-clj

jar:
	clojure -Ajar

release-patch:
	clojure -Arelease patch

release-minor:
	clojure -Arelease minor

release-major:
	clojure -Arelease major

deploy:
	clojure -Adeploy

deploy-patch: test jar release-patch deploy
