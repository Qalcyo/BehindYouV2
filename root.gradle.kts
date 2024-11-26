plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.12.2-forge"(11202, "srg") {
        "1.8.9-forge"(10809, "srg")
    }
}