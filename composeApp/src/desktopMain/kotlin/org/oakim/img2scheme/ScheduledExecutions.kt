package org.oakim.img2scheme

import java.util.LinkedList
import java.util.Queue

object ScheduledExecutions {
    val afterImageDraw: Queue<() -> Unit> = LinkedList()
}