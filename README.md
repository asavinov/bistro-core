* [How to build](#how-to-build)
* [How to use](#how-to-use)

# How to build

From the project folder (`bistro/core`) execute the following commands to clean, build and publish the artifact:

```console
$ gradlew clean
$ gradlew build
$ gradlew publish
```

The artifact will be stored in your local repository from where it will be available to other projects.

# How to use

In order to include this artifact into your project add the following lines to dependencies of your `build.gradle`:

```groovy
dependencies {
    compile("bistro:bistro-core:0.1.0")

    // Other dependencies
}
```

# License

See LICENSE file in the project directory: https://github.com/asavinov/bistro-core/blob/master/LICENSE
