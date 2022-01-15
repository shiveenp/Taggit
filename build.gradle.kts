tasks.register("stage") {
    description = "Sets the app for staging before deloyment"
    dependsOn(":backend:bootJar")
}π