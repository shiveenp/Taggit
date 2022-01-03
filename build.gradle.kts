tasks.register("stage") {
    description = "Sets the app for staging"
    dependsOn(":backend:bootJar")
}