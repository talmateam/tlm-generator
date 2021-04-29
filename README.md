# microgenerator

Generador de plantillas. Internamente ejecuta guiter8, genera plantillas cargadas en repositorios git.
El servidor que aloje a la aplicaci�n necesita tener instalado conscript y giter8


## Compile
```shell
mvn -s devops/settings.xml clean -B install
```

## Generate and deploy image    
               
```shell
docker build -t="acreu2c001assidev01.azurecr.io/tlm-generator-app:1.0.0-0-ayni-SNAPSHOT" --build-arg artifact_id=tlm-generator-app --build-arg artifact_version=1.0.0-0-ayni-SNAPSHOT .

docker push acreu2c001assidev01.azurecr.io/tlm-generator-app:1.0.0-0-ayni-SNAPSHOT
```

## Deploy in AKS

```shell
kubectl create -f microgenerator.yml

kubectl delete -f microgenerator.yml
```
