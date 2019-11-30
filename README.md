keepmeawake

<img src="https://github.com/akulinski/keepmeawake/workflows/Java%20CI/badge.svg">



# Mucho Importante

* Cnfigure profile in intellij run. To mock data use profile dev

* Login to github docker docker login docker.pkg.github.com --username XYZ. Generate docker access token and pass it as password
* AWS Security Credentials: These are our access keys that allow us to make programmatic calls to AWS API actions. We can get these credentials in two ways, either by using AWS root account credentials from access keys section of Security Credentials page or by using IAM user credentials from IAM console
Generate new access key download csv and set enviroment variables ACCESS_KEY and SECRET_KEY
# How to run

In docker folder

1. docker-compose -f mongo.yaml -d 

2. docker-compose -f redis.yaml -d

add spring boot profiles dev,log-web
