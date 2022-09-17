# Taggit

Taggit is a starred repositories organisation tool for Github. It was built to enable easy tagging and 
organisation of repositories in your Github stars and allows easy recall for libraries or cool projects you found but
forgot the name of.

The project runs on springboot backend core and a VueJS frontend. It can be easily deployed to your favourite cloud provider 
or via a PAAS.

## Project setup

The project is split into backend and front end. 

The backend is written using Kotlin and Spring.

The frontend is written using [VueJs](https://vuejs.org/) and accompanying libraries like Vuex and VueRouter etc.

## Packing and Deploys

There are three distinct ways to deploy the app. 

- Docker Image
- Docker File
- Jar File

### Docker Image

The most up to date docker image can be found in the [releases](https://github.com/shiveenp/Taggit/releases) section.

Use that image to deploy or run the app locally.

### Docker File

The project also has the root docker file that can be used to build the app locally. 

### Jar File

The whole app can be deployed via the compiled JAR as a monolithic binary i.e. the frontend and the server run from the
same Springboot Netty server. This simplifies deployment as you don't have to worry about serving the HTML/CSS/JS assets
from elsewhere.

The whole app can be packed by running the following command inside the repo root:

`./gradlew stage`

This command will build the frontend and the backend and move all the files inside a static directory for backend ay build time.

### Deployment Secrets

You will need to set following variables when deploying:

```
DATABASE_URL=<add your postgres database url here>
DATABASE_USERNAME=<add your database username here>
DATABASE_PASSWORD=<add your database password here>
GITHUB_USERNAME=<add your github username here>
GITHUB_TOKEN=<add your github personal access token here>
APP_PASSWORD=<this the password to get it, adds an extra layer of security when deployed publicly on web>
```

More information on how to create a github personal access token can be found [here](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

## Local Dev

### Running the backend

You can run the backend by directly running the main class in the App.kt file or preferably using Docker.

Since we use springboot the easiest way to run backend is to run the following command in the root of the project.

`./gradlew :backend:bootRun`

For running with Docker ensure you have docker installed, for Mac that can be done via brew:

```shell script
brew install --cask docker
```

Once docker is installed, navigate to the backend folder and run docker compose:

```shell
docker-compose up
```

To run for testing postgres do:

```shell
docker-compose --profile test up
```

Once Postgres is started, create a table named `taggit` inside your local docker postgres.

Once that is successfully completed, we can fire up the front end.

### Running the front end

First navigate into the frontend directory and install all the node modules:

```shell script
npm install
```

Once done, fire up the frontend using vue cli:

```shell script
npm run serve
```

Now you can navigate to http://localhost:3000 on your browser to see the SPA. Please click `Sync` the first time to have all your repos from github synced.

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```