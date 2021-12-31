plugins {
    id("org.siouan.frontend-jdk11") version "6.0.0"
}

frontend {
    nodeVersion.set("14.17.3")
    assembleScript.set("run build")
}