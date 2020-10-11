package com.littledemon.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author https://github.com/funnywolfdadada
 * @since 2020/9/5
 */
open class MyTask: DefaultTask() {

    @TaskAction
    fun action() {
        println("my task run")
    }

}