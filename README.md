# La directive includeBuild de Gradle

## Objectif
L'objectif est d'étudier la directive includeBuild de Gradle dans l'objectif de pouvoir gérer une  application composée de multiples projets sur des dépêts distincts au sein de la même instance de l'IDE.
Outre la simplification de l'usage de l'IDE, cela aiderait au débogage (Accès au code source au lieu du code décompilé) et permet d'exécuter le code sans besoin d'une publication des projets des autres dépôts.

La directive includeBuild ne remplace pas l'usage de *dependencies{}*.
Il ne fait qu'ajouter la dépendance de *build* entre ces projets.
Par exemple, si le projet A déclare la directive includeBuild(B) alors le build de A impliquera le build préalable de B. 


## Contexte des tests
Pour analyser et vérifier le fonctionnement de la directive includeBuild, une application multi-modules et multi-dépôts est utilisée.

La composition de l'application est la suivante :
* Le projet **hello-service** qui implémente un service permettant de générer un message de bienvenue
* le projet **person-provider** qui implémente un service fournissant une liste de noms de personnes
* le projet **hello-app** qui utilise les deux projets précédents pour afficher 
    
Ces projets sont répartis dans deux dépôts distincts :
* Le dépôt repo-a 
    * hello-app
* Le dépôt repo-b contient :
    * hello-service
    * person-provider



## Test A : projets indépendants

Chaque projet contient des fichiers *settings.gradle* (vides) et *build.gradle*.
Les dépendances sont déclarées dans le *build.gradle* de **hello-app** :
```gradle
dependencies {
    implementation "io.gofannon.gradle_large_project:hello-service:1.0-SNAPSHOT"
    implementation "io.gofannon.gradle_large_project:person-provider:1.0-SNAPSHOT"
}
```

Ainsi quand des modifications sont effectuées dans les projets **hello-service** et **person-provider**, il faut effectuer 
un build et une publication dans un dépôt d'artefact (MavenLocal, artifactory, ...) afin que **hello-app** puisse les récupérer.

Pour prendre en compte l'ajout d'une personne dans le projet **person-provider**, les instructions suivantes sont nécessaires :
```bash
$> cd repo-b/person-provider
$> gradle build publish
$> cd ../../repo-a/hello-app
$> gradle run
```

Le code source est disponible ici : [test-a](./test-a)



## Test B : projets dépendants par le build
Il s'agit de modifier le projet **hello-app** afin qu'il dépende des sources des autres projets.
Ainsi lorsque **hello-app** est "buildé" et "exécutée", ce seront les sources des autres projets et non plus en se basant 
sur le contenu d'un dépôt d'artefact intermédiaire

Pour cela, il faut mettre à jour le fichier *settings.gradle* de **hello-app** de la manière suivante :
```gradle
includeBuild("../../repo-b/hello-service")
includeBuild("../../repo-b/person-provider")
```

IntelliJ affiche l'ensemble des projets et de leurs sources au sein de la même instance : la fenêtre *Gradle* affiche 
tous les projets et leurs tâches. 

Pour prendre en compte l'ajout d'une personne dans le projet **person-provider**, les instructions suivantes sont nécessaires :
```bash
$> cd repo-a/hello-app
$> gradle run
```

Ainsi, il n'est plus nécessaire de builder et publier le projet **person-provider**. 
Comme il n'y a pas eu de publication le dépôt d'artefacts n'est pas impacté, il contient la précédente version de 
l'artefact **person-provider**.

Cette solution n'est cependant pas satisfaisante car le lien entre **hello-app** et les librairies **person-provider** 
et **hello-service** est nécessaire pour l'exploiter. 
De ce fait, il n'est pas possible de cloner et modifier le projet **hello-app** sans cloner également les projets 
**person-provider** et **hello-service**.

Ce lien n'est pas problématique en plein développement d'une nouvelle application, mais pour des tâches rapides 
(Correction de blogue, MCO, ...) ou de grands projets, il devient trop contraignant.

Ainsi une telle solution est pratique sur des petits projets mais ne l'est pas sur des projets importants ou des phases 
discontinues de développement.

Le code source est disponible ici : [test-b](./test-b)



## Test C : un projet pour les unifier tous

Une autre approche est de créer un autre projet, vide, qui contiendra uniquement des liens *includeBuild* vers l'ensemble 
des projets désirés.

Ce projet racine, appelé **full-app** dans notre exemple, est situé à part, dans un dépôt Git distinct des autres.

Son fichier *build.gradle* est minimale :
```gradle
plugins {
    id 'idea'
}

group = "io.gofannon.gradle_large_project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

wrapper {
    gradleVersion = '7.6'
}
```

Et son fichier *settings.gradle* contient la liste des includeBuild désirés :
```gradle
includeBuild("../../repo-b/hello-service")
includeBuild("../../repo-b/person-provider")
includeBuild("../../repo-a/hello-app")
```

Cette approche fonctionne comme celle de *test-b* mais l'usage d'un projet dédié aux includeBuild permet d'externaliser 
les dépendances de sources entre projets.
Cette approche est adaptative, il suffit de déclarer uniquement les includeBuild des projets que l'on souhaite modifier 
ou debugger.  

Le code source est disponible ici : [test-c](./test-c)



## Conclusion
La directive *includeBuild* utilisée depuis un projet externe remplit les objectifs fixés :
* affichage de tous les projets souhaités dans la même instance de l'IDE
  * cela inclut le code source
* exécution du code avec prise des modifications locales sans nécessité de publication dans un dépôt d'artefact intermédiaire



## Sources
* S1 [Gradle Structuring Large Projects](https://docs.gradle.org/current/userguide/structuring_software_products.html)
