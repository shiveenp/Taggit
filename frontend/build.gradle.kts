plugins {
    id("org.siouan.frontend-jdk11") version "6.0.0"
}

frontend {
    nodeDistributionUrlPathPattern.set("vVERSION/node-vVERSION-darwin-x64.TYPE")
    nodeVersion.set("14.17.3")
    assembleScript.set("run build")
}