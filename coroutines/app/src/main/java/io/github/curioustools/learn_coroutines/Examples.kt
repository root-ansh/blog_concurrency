package io.github.curioustools.learn_coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentSkipListSet

object Examples {

    fun String.change(coroutine: String): String {
        return this.replace(" ", "").replace("DefaultDispatcher-worker", coroutine)
            .replace("@coroutine#", "_c")
    }

    fun testThreadPoolSharing(scope: CoroutineScope) {
        scope.launch {
            val threadNames = ConcurrentSkipListSet<String>()
            val list = mutableListOf<Job>()
            val x = 150
            val start = System.currentTimeMillis()
            repeat(x) { i ->
                val j2 = launch(Dispatchers.Default) {
                    delay(10)
                    val name = Thread.currentThread().name.change("def")
                    println("Default Job " + (if (threadNames.contains(name)) "[$i] : REUSED thread: $name" else "[$i]: new thread: $name"))
                    threadNames.add(name)
                }
                val j1 = launch(Dispatchers.IO) {
                    delay(10)
                    val name = Thread.currentThread().name.change("io")
                    println("IO Job" + (if (threadNames.contains(name)) "[$i]: REUSED thread: $name" else "[$i]: new thread: $name"))
                    threadNames.add(name)

                }
                list.add(j1)
                list.add(j2)
            }
            println("${x.times(2)} jobs added")
            joinAll(*list.toTypedArray())
            println(" ${x.times(2)}  jobs executed taking ${System.currentTimeMillis() - start}ms : names(${threadNames.size}) : $threadNames")
        }
        //OUTPUT
        /**
        263 10627-10627   I  300 jobs added
        346 10627-10664   I  Default Job [0]: new thread: def-10
        380 10627-10664   I  IO Job[3]: new thread: io-10
        381 10627-10677   I  Default Job [6]: new thread: def-22
        381 10627-10664   I  IO Job[7]: REUSED thread: io-10
        382 10627-10677   I  IO Job[10]: new thread: io-22
        383 10627-10700   I  IO Job[4]: new thread: io-44
        387 10627-10705   I  IO Job[12]: new thread: io-49
        387 10627-10705   I  IO Job[11]: REUSED thread: io-49
        400 10627-10673   I  Default Job [7]: new thread: def-18
        400 10627-10692   I  IO Job[0]: new thread: io-37
        400 10627-10684   I  Default Job [1]: new thread: def-29
        400 10627-10657   I  Default Job [10]: new thread: def-4
        401 10627-10684   I  Default Job [4] : REUSED thread: def-29
        401 10627-10663   I  Default Job [2]: new thread: def-9
        401 10627-10684   I  Default Job [12] : REUSED thread: def-29
        402 10627-10663   I  Default Job [13] : REUSED thread: def-9
        402 10627-10684   I  Default Job [14] : REUSED thread: def-29
        402 10627-10663   I  Default Job [15] : REUSED thread: def-9
        402 10627-10684   I  Default Job [16] : REUSED thread: def-29
        402 10627-10663   I  Default Job [17] : REUSED thread: def-9
        402 10627-10684   I  Default Job [18] : REUSED thread: def-29
        403 10627-10663   I  Default Job [19] : REUSED thread: def-9
        403 10627-10677   I  IO Job[13]: REUSED thread: io-22
        405 10627-10681   I  IO Job[20]: new thread: io-26
        408 10627-10667   I  Default Job [20]: new thread: def-13
        409 10627-10727   I  IO Job[9]: new thread: io-62
        409 10627-10665   I  IO Job[8]: new thread: io-11
        410 10627-10712   I  IO Job[17]: new thread: io-56
        411 10627-10727   I  IO Job[33]: REUSED thread: io-62
        414 10627-10741   I  IO Job[2]: new thread: io-71
        417 10627-10690   I  IO Job[21]: new thread: io-35
        418 10627-10654   I  Default Job [3]: new thread: def-1
        421 10627-10728   I  IO Job[42]: new thread: io-63
        422 10627-10688   I  Default Job [24]: new thread: def-33
        422 10627-10740   I  IO Job[37]: new thread: io-70
        422 10627-10688   I  IO Job[39]: new thread: io-33
        422 10627-10728   I  IO Job[40]: REUSED thread: io-63
        423 10627-10681   I  IO Job[38]: REUSED thread: io-26
        428 10627-10683   I  IO Job[22]: new thread: io-28
        428 10627-10678   I  IO Job[23]: new thread: io-23
        433 10627-10689   I  IO Job[26]: new thread: io-34
        433 10627-10724   I  Default Job [9]: new thread: def-59
        435 10627-10705   I  IO Job[14]: REUSED thread: io-49
        435 10627-10692   I  IO Job[43]: REUSED thread: io-37
        436 10627-10692   I  IO Job[48]: REUSED thread: io-37
        436 10627-10742   I  IO Job[45]: new thread: io-72
        436 10627-10696   I  IO Job[30]: new thread: io-40
        437 10627-10659   I  IO Job[31]: new thread: io-5
        437 10627-10669   I  IO Job[29]: new thread: io-15
        438 10627-10726   I  IO Job[32]: new thread: io-61
        438 10627-10739   I  IO Job[15]: new thread: io-69
        439 10627-10707   I  IO Job[5]: new thread: io-51
        440 10627-10731   I  Default Job [8]: new thread: def-65
        442 10627-10703   I  IO Job[28]: new thread: io-47
        442 10627-10656   I  IO Job[16]: new thread: io-3
        443 10627-10669   I  IO Job[50]: REUSED thread: io-15
        443 10627-10656   I  IO Job[51]: REUSED thread: io-3
        443 10627-10696   I  IO Job[54]: REUSED thread: io-40
        443 10627-10680   I  IO Job[41]: new thread: io-25
        445 10627-10663   I  Default Job [22] : REUSED thread: def-9
        446 10627-10673   I  IO Job[34]: new thread: io-18
        447 10627-10661   I  IO Job[47]: new thread: io-7
        447 10627-10728   I  IO Job[46]: REUSED thread: io-63
        448 10627-10655   I  IO Job[49]: new thread: io-2
        449 10627-10666   I  IO Job[25]: new thread: io-12
        450 10627-10655   I  IO Job[55]: REUSED thread: io-2
        450 10627-10692   I  IO Job[18]: REUSED thread: io-37
        450 10627-10655   I  IO Job[59]: REUSED thread: io-2
        450 10627-10692   I  IO Job[60]: REUSED thread: io-37
        450 10627-10691   I  IO Job[27]: new thread: io-36
        451 10627-10689   I  IO Job[63]: REUSED thread: io-34
        451 10627-10704   I  IO Job[61]: new thread: io-48
        451 10627-10689   I  IO Job[65]: REUSED thread: io-34
        451 10627-10703   I  IO Job[53]: REUSED thread: io-47
        451 10627-10705   I  IO Job[66]: REUSED thread: io-49
        452 10627-10691   I  IO Job[68]: REUSED thread: io-36
        453 10627-10666   I  IO Job[56]: REUSED thread: io-12
        455 10627-10725   I  IO Job[19]: new thread: io-60
        456 10627-10680   I  IO Job[58]: REUSED thread: io-25
        456 10627-10707   I  IO Job[57]: REUSED thread: io-51
        456 10627-10726   I  IO Job[52]: REUSED thread: io-61
        460 10627-10655   I  IO Job[62]: REUSED thread: io-2
        462 10627-10739   I  Default Job [5]: new thread: def-69
        462 10627-10669   I  IO Job[36]: REUSED thread: io-15
        464 10627-10664   I  IO Job[1]: REUSED thread: io-10
        465 10627-10689   I  IO Job[70]: REUSED thread: io-34
        465 10627-10683   I  IO Job[69]: REUSED thread: io-28
        467 10627-10703   I  IO Job[72]: REUSED thread: io-47
        467 10627-10681   I  IO Job[73]: REUSED thread: io-26
        467 10627-10678   I  IO Job[75]: REUSED thread: io-23
        467 10627-10686   I  IO Job[24]: new thread: io-31
        469 10627-10690   I  Default Job [21]: new thread: def-35
        469 10627-10715   I  IO Job[6]: new thread: io-58
        470 10627-10690   I  IO Job[74]: REUSED thread: io-35
        470 10627-10657   I  Default Job [11] : REUSED thread: def-4
        471 10627-10715   I  IO Job[76]: REUSED thread: io-58
        472 10627-10740   I  IO Job[44]: REUSED thread: io-70
        473 10627-10692   I  IO Job[64]: REUSED thread: io-37
        482 10627-10669   I  IO Job[78]: REUSED thread: io-15
        483 10627-10681   I  IO Job[83]: REUSED thread: io-26
        483 10627-10690   I  IO Job[79]: REUSED thread: io-35
        483 10627-10724   I  IO Job[84]: new thread: io-59
        483 10627-10690   I  IO Job[89]: REUSED thread: io-35
        484 10627-10715   I  IO Job[86]: REUSED thread: io-58
        484 10627-10678   I  IO Job[80]: REUSED thread: io-23
        484 10627-10683   I  IO Job[81]: REUSED thread: io-28
        485 10627-10654   I  IO Job[87]: new thread: io-1
        488 10627-10685   I  Default Job [25]: new thread: def-30
        489 10627-10685   I  Default Job [26] : REUSED thread: def-30
        489 10627-10664   I  IO Job[94]: REUSED thread: io-10
        489 10627-10657   I  IO Job[88]: new thread: io-4
        491 10627-10681   I  IO Job[85]: REUSED thread: io-26
        491 10627-10699   I  IO Job[35]: new thread: io-43
        491 10627-10724   I  IO Job[93]: REUSED thread: io-59
        491 10627-10715   I  IO Job[92]: REUSED thread: io-58
        494 10627-10664   I  Default Job [28] : REUSED thread: def-10
        494 10627-10686   I  IO Job[97]: REUSED thread: io-31
        494 10627-10663   I  IO Job[98]: new thread: io-9
        495 10627-10709   I  Default Job [29]: new thread: def-53
        495 10627-10656   I  IO Job[102]: REUSED thread: io-3
        496 10627-10667   I  Default Job [23] : REUSED thread: def-13
        496 10627-10689   I  Default Job [27]: new thread: def-34
        498 10627-10688   I  IO Job[100]: REUSED thread: io-33
        498 10627-10664   I  Default Job [30] : REUSED thread: def-10
        499 10627-10664   I  Default Job [31] : REUSED thread: def-10
        499 10627-10686   I  IO Job[103]: REUSED thread: io-31
        500 10627-10686   I  IO Job[105]: REUSED thread: io-31
        500 10627-10664   I  Default Job [32] : REUSED thread: def-10
        500 10627-10663   I  IO Job[104]: REUSED thread: io-9
        500 10627-10672   I  IO Job[107]: new thread: io-17
        500 10627-10663   I  IO Job[113]: REUSED thread: io-9
        501 10627-10672   I  IO Job[99]: REUSED thread: io-17
        501 10627-10663   I  IO Job[115]: REUSED thread: io-9
        501 10627-10664   I  Default Job [33] : REUSED thread: def-10
        501 10627-10711   I  IO Job[108]: new thread: io-55
        501 10627-10666   I  IO Job[110]: REUSED thread: io-12
        502 10627-10682   I  IO Job[106]: new thread: io-27
        502 10627-10666   I  IO Job[120]: REUSED thread: io-12
        502 10627-10702   I  IO Job[71]: new thread: io-46
        502 10627-10664   I  Default Job [35] : REUSED thread: def-10
        502 10627-10713   I  IO Job[114]: new thread: io-57
        503 10627-10657   I  IO Job[122]: REUSED thread: io-4
        503 10627-10713   I  IO Job[123]: REUSED thread: io-57
        503 10627-10697   I  IO Job[125]: new thread: io-41
        504 10627-10711   I  IO Job[111]: REUSED thread: io-55
        505 10627-10665   I  IO Job[118]: REUSED thread: io-11
        506 10627-10711   I  IO Job[119]: REUSED thread: io-55
        506 10627-10711   I  IO Job[124]: REUSED thread: io-55
        506 10627-10711   I  IO Job[126]: REUSED thread: io-55
        507 10627-10697   I  IO Job[127]: REUSED thread: io-41
        507 10627-10725   I  IO Job[116]: REUSED thread: io-60
        510 10627-10666   I  IO Job[121]: REUSED thread: io-12
        510 10627-10664   I  Default Job [36] : REUSED thread: def-10
        511 10627-10664   I  Default Job [37] : REUSED thread: def-10
        512 10627-10679   I  IO Job[95]: new thread: io-24
        513 10627-10696   I  IO Job[117]: REUSED thread: io-40
        513 10627-10703   I  IO Job[77]: REUSED thread: io-47
        514 10627-10739   I  Default Job [34] : REUSED thread: def-69
        514 10627-10703   I  IO Job[128]: REUSED thread: io-47
        525 10627-10727   I  IO Job[82]: REUSED thread: io-62
        525 10627-10686   I  IO Job[101]: REUSED thread: io-31
        527 10627-10686   I  IO Job[129]: REUSED thread: io-31
        532 10627-10693   I  IO Job[131]: new thread: io-38
        533 10627-10660   I  IO Job[130]: new thread: io-6
        534 10627-10659   I  IO Job[90]: REUSED thread: io-5
        535 10627-10742   I  IO Job[67]: REUSED thread: io-72
        536 10627-10692   I  IO Job[96]: REUSED thread: io-37
        537 10627-10670   I  IO Job[91]: new thread: io-16
        545 10627-10660   I  IO Job[135]: REUSED thread: io-6
        546 10627-10724   I  IO Job[134]: REUSED thread: io-59
        548 10627-10699   I  IO Job[133]: REUSED thread: io-43
        549 10627-10683   I  IO Job[136]: REUSED thread: io-28
        549 10627-10698   I  IO Job[143]: new thread: io-42
        549 10627-10661   I  IO Job[148]: REUSED thread: io-7
        550 10627-10715   I  IO Job[149]: REUSED thread: io-58
        550 10627-10669   I  IO Job[141]: REUSED thread: io-15
        550 10627-10715   I  Default Job [38]: new thread: def-58
        550 10627-10673   I  IO Job[109]: REUSED thread: io-18
        550 10627-10715   I  Default Job [40] : REUSED thread: def-58
        551 10627-10715   I  Default Job [41] : REUSED thread: def-58
        551 10627-10715   I  Default Job [42] : REUSED thread: def-58
        551 10627-10715   I  Default Job [43] : REUSED thread: def-58
        552 10627-10715   I  Default Job [45] : REUSED thread: def-58
        552 10627-10715   I  Default Job [47] : REUSED thread: def-58
        553 10627-10715   I  Default Job [48] : REUSED thread: def-58
        558 10627-10726   I  IO Job[137]: REUSED thread: io-61
        559 10627-10742   I  IO Job[138]: REUSED thread: io-72
        561 10627-10663   I  IO Job[144]: REUSED thread: io-9
        561 10627-10701   I  IO Job[147]: new thread: io-45
        561 10627-10715   I  Default Job [49] : REUSED thread: def-58
        561 10627-10670   I  IO Job[139]: REUSED thread: io-16
        566 10627-10655   I  Default Job [50]: new thread: def-2
        569 10627-10739   I  Default Job [46] : REUSED thread: def-69
        569 10627-10724   I  IO Job[146]: REUSED thread: io-59
        570 10627-10704   I  IO Job[142]: REUSED thread: io-48
        571 10627-10655   I  Default Job [51] : REUSED thread: def-2
        572 10627-10715   I  Default Job [44] : REUSED thread: def-58
        572 10627-10661   I  Default Job [39]: new thread: def-7
        572 10627-10715   I  Default Job [52] : REUSED thread: def-58
        572 10627-10661   I  Default Job [53] : REUSED thread: def-7
        573 10627-10715   I  Default Job [54] : REUSED thread: def-58
        573 10627-10661   I  Default Job [55] : REUSED thread: def-7
        573 10627-10715   I  Default Job [56] : REUSED thread: def-58
        575 10627-10664   I  IO Job[145]: REUSED thread: io-10
        580 10627-10693   I  IO Job[140]: REUSED thread: io-38
        582 10627-10707   I  Default Job [57]: new thread: def-51
        583 10627-10680   I  Default Job [59]: new thread: def-25
        583 10627-10680   I  Default Job [60] : REUSED thread: def-25
        583 10627-10680   I  Default Job [61] : REUSED thread: def-25
        584 10627-10680   I  Default Job [63] : REUSED thread: def-25
        584 10627-10737   I  Default Job [62]: new thread: def-67
        585 10627-10664   I  Default Job [66] : REUSED thread: def-10
        585 10627-10737   I  Default Job [67] : REUSED thread: def-67
        586 10627-10692   I  Default Job [65]: new thread: def-37
        587 10627-10737   I  Default Job [69] : REUSED thread: def-67
        587 10627-10680   I  Default Job [64] : REUSED thread: def-25
        588 10627-10737   I  Default Job [70] : REUSED thread: def-67
        589 10627-10692   I  Default Job [71] : REUSED thread: def-37
        591 10627-10664   I  Default Job [68] : REUSED thread: def-10
        593 10627-10680   I  Default Job [72] : REUSED thread: def-25
        593 10627-10737   I  Default Job [73] : REUSED thread: def-67
        594 10627-10692   I  Default Job [74] : REUSED thread: def-37
        596 10627-10692   I  Default Job [77] : REUSED thread: def-37
        599 10627-10692   I  Default Job [78] : REUSED thread: def-37
        599 10627-10707   I  Default Job [58] : REUSED thread: def-51
        600 10627-10707   I  Default Job [80] : REUSED thread: def-51
        600 10627-10685   I  IO Job[112]: new thread: io-30
        602 10627-10692   I  Default Job [81] : REUSED thread: def-37
        603 10627-10737   I  Default Job [79] : REUSED thread: def-67
        604 10627-10707   I  Default Job [82] : REUSED thread: def-51
        604 10627-10737   I  Default Job [84] : REUSED thread: def-67
        604 10627-10707   I  Default Job [85] : REUSED thread: def-51
        605 10627-10707   I  Default Job [87] : REUSED thread: def-51
        605 10627-10664   I  Default Job [76] : REUSED thread: def-10
        614 10627-10680   I  Default Job [90] : REUSED thread: def-25
        614 10627-10654   I  Default Job [83] : REUSED thread: def-1
        614 10627-10737   I  Default Job [88] : REUSED thread: def-67
        615 10627-10737   I  Default Job [93] : REUSED thread: def-67
        616 10627-10664   I  Default Job [91] : REUSED thread: def-10
        616 10627-10692   I  Default Job [86] : REUSED thread: def-37
        616 10627-10674   I  IO Job[132]: new thread: io-19
        617 10627-10692   I  Default Job [95] : REUSED thread: def-37
        618 10627-10692   I  Default Job [97] : REUSED thread: def-37
        619 10627-10737   I  Default Job [94] : REUSED thread: def-67
        619 10627-10654   I  Default Job [99] : REUSED thread: def-1
        620 10627-10737   I  Default Job [100] : REUSED thread: def-67
        620 10627-10737   I  Default Job [102] : REUSED thread: def-67
        621 10627-10737   I  Default Job [103] : REUSED thread: def-67
        621 10627-10692   I  Default Job [98] : REUSED thread: def-37
        622 10627-10707   I  Default Job [89] : REUSED thread: def-51
        622 10627-10737   I  Default Job [104] : REUSED thread: def-67
        622 10627-10707   I  Default Job [106] : REUSED thread: def-51
        622 10627-10737   I  Default Job [107] : REUSED thread: def-67
        623 10627-10707   I  Default Job [108] : REUSED thread: def-51
        623 10627-10737   I  Default Job [109] : REUSED thread: def-67
        624 10627-10684   I  Default Job [75] : REUSED thread: def-29
        625 10627-10664   I  Default Job [96] : REUSED thread: def-10
        626 10627-10680   I  Default Job [92] : REUSED thread: def-25
        627 10627-10664   I  Default Job [110] : REUSED thread: def-10
        628 10627-10664   I  Default Job [111] : REUSED thread: def-10
        628 10627-10707   I  Default Job [112] : REUSED thread: def-51
        630 10627-10737   I  Default Job [113] : REUSED thread: def-67
        630 10627-10680   I  Default Job [114] : REUSED thread: def-25
        630 10627-10737   I  Default Job [115] : REUSED thread: def-67
        633 10627-10664   I  Default Job [117] : REUSED thread: def-10
        634 10627-10664   I  Default Job [120] : REUSED thread: def-10
        635 10627-10715   I  Default Job [119] : REUSED thread: def-58
        636 10627-10707   I  Default Job [116] : REUSED thread: def-51
        636 10627-10667   I  Default Job [118] : REUSED thread: def-13
        637 10627-10692   I  Default Job [105] : REUSED thread: def-37
        637 10627-10667   I  Default Job [125] : REUSED thread: def-13
        637 10627-10728   I  Default Job [123]: new thread: def-63
        638 10627-10664   I  Default Job [122] : REUSED thread: def-10
        638 10627-10654   I  Default Job [101] : REUSED thread: def-1
        638 10627-10715   I  Default Job [126] : REUSED thread: def-58
        639 10627-10664   I  Default Job [127] : REUSED thread: def-10
        640 10627-10684   I  Default Job [121] : REUSED thread: def-29
        640 10627-10674   I  Default Job [129]: new thread: def-19
        645 10627-10667   I  Default Job [130] : REUSED thread: def-13
        645 10627-10674   I  Default Job [131] : REUSED thread: def-19
        645 10627-10664   I  Default Job [128] : REUSED thread: def-10
        645 10627-10654   I  Default Job [132] : REUSED thread: def-1
        645 10627-10667   I  Default Job [134] : REUSED thread: def-13
        646 10627-10674   I  Default Job [136] : REUSED thread: def-19
        646 10627-10667   I  Default Job [135] : REUSED thread: def-13
        646 10627-10654   I  Default Job [137] : REUSED thread: def-1
        650 10627-10737   I  Default Job [139] : REUSED thread: def-67
        651 10627-10654   I  Default Job [140] : REUSED thread: def-1
        652 10627-10737   I  Default Job [141] : REUSED thread: def-67
        653 10627-10684   I  Default Job [133] : REUSED thread: def-29
        654 10627-10696   I  Default Job [143]: new thread: def-40
        655 10627-10696   I  Default Job [145] : REUSED thread: def-40
        655 10627-10692   I  Default Job [138] : REUSED thread: def-37
        655 10627-10696   I  Default Job [147] : REUSED thread: def-40
        656 10627-10654   I  Default Job [144] : REUSED thread: def-1
        657 10627-10715   I  Default Job [124] : REUSED thread: def-58
        657 10627-10707   I  Default Job [142] : REUSED thread: def-51
        659 10627-10715   I  Default Job [148] : REUSED thread: def-58
        659 10627-10715   I  Default Job [146] : REUSED thread: def-58
        659 10627-10684   I  Default Job [149] : REUSED thread: def-29
        749 10627-10627   I   300  jobs executed taking 541ms : names(81) : [def-1, def-10, def-13, def-18, def-19, def-2, def-22, def-25, def-29, def-30, def-33, def-34, def-35, def-37, def-4, def-40, def-51, def-53, def-58, def-59, def-63, def-65, def-67, def-69, def-7, def-9, io-1, io-10, io-11, io-12, io-15, io-16, io-17, io-18, io-19, io-2, io-22, io-23, io-24, io-25, io-26, io-27, io-28, io-3, io-30, io-31, io-33, io-34, io-35, io-36, io-37, io-38, io-4, io-40, io-41, io-42, io-43, io-44, io-45, io-46, io-47, io-48, io-49, io-5, io-51, io-55, io-56, io-57, io-58, io-59, io-6, io-60, io-61, io-62, io-63, io-69, io-7, io-70, io-71, io-72, io-9]

         */
    }


    fun testExecutions1(scope: CoroutineScope){
//        scope.launch {
//            launch(Dispatchers.Default){ delay(20);println("L01") }
//            launch(Dispatchers.IO){ delay(20);println("L03") }
//            launch(Dispatchers.Default){ delay(20);println("L05") }
//            val o1 = async { delay(10);println("L07");"X07" }
//            val o2 = async{ delay(10);println("L09");"X09" }
//            println("L02")
//            println("L04")
//            println("L06")
//            println("LO8")
//            println("L10")
//            withContext(Dispatchers.Default){delay(15);println("L11")}
//            println("L12")
//            withContext(Dispatchers.IO){delay(15);println("L13")}
//            println("L14")
//            withContext(Dispatchers.Default){delay(15);println("L15")}
//            println("L16")
//            o1.await().let { println(it) }
//            o2.await().let { println(it) }
//            println("L17")// text inside scope
//        }
        scope.launch {
            launch(Dispatchers.Default){ delay(500);println("L01") }
            println("L02")
            launch(Dispatchers.IO){ delay(500);println("L03") }
            println("L04")
            launch(Dispatchers.Default){ delay(500);println("L05") }
            println("L06")
            val o1 = async { delay(250);println("L07");"X07" }
            println("LO8")
            val o2 = async{ delay(250);println("L09");"X09" }
            println("L10")
            withContext(Dispatchers.Default){delay(10);println("L11")}
            println("L12")
            withContext(Dispatchers.IO){delay(10);println("L13")}
            println("L14")
            withContext(Dispatchers.Default){delay(10);println("L15")}
            println("L16")
            o1.await().let { println(it) }
            o2.await().let { println(it) }
            println("L17")// text inside scope
        }
        println("L18")// text outside scope

        //L18  // text outside scope got executed first, scope executions will happen later
        //L02 ---┒
        //L04    |
        //L06    |  -- these lines got evaluated first, because async/launch call don't wait to complete and let the parent move onto next line
        //LO8    |
        //L10 ---┚
        //L11---┒
        //L12   |
        //L13   |  -- these lines got executed together because withcontext is a suspending line and will not let the parent run next line unless complete
        //L14   |
        //L15   |
        //L16---┚
        //L07---┒ -- Async got complete but results are awaited
        //L09---┚
        //X07---┒ -- await results
        //X09---┚
        //L17    -- this will always run post await because await is a suspending line and will not let the parent run next line unless executed first
        //L01---┒
        //L03   |--results of launch
        //L05---┚
    }


    // a corutine state could be NEW,ACTIVE COMPLETING,COMPLETED, CANCELLIN, CANCELLED
    fun testJobState(scope: CoroutineScope) = scope.launch {
        println("launched jobs")
        val j1 = launch() {
            println("j1: started")
            delay(500)
            println("j1: ended")
        }

        val j2 = launch() {
            println("j2: started")
            delay(1000)
            println("j2: ended")
        }

        println("post 0ms")
        j1.state().let { println("current j1 state:$it") }
        j2.state().let { println("current j2 state:$it") }

        delay(250)
        println("post 250ms")
        j1.state().let { println("current j1 state:$it") }
        j2.state().let { println("current j2 state:$it") }

        delay(250)
        println("post 500ms")
        j1.state().let { println("current j1 state:$it") }
        j2.state().let { println("current j2 state:$it") }

        delay(250)
        println("post 750ms")
        j1.state().let { println("current j1 state:$it") }
        j2.state().let { println("current j2 state:$it") }

        delay(250)
        println("post 1000ms")
        j1.state().let { println("current j1 state:$it") }
        j2.state().let { println("current j2 state:$it") }


        delay(250)
        println("post 1200ms")
        j1.state().let { println("current j1 state:$it") }
        j2.state().let { println("current j2 state:$it") }

        //launched jobs
        //post 0ms
        //current j1 state:ACTIVE
        //current j2 state:ACTIVE
        //j1: started
        //j2: started
        //post 250ms
        //current j1 state:ACTIVE
        //current j2 state:ACTIVE
        //j1: ended
        //post 500ms
        //current j1 state:COMPLETED
        //current j2 state:ACTIVE
        //post 750ms
        //current j1 state:COMPLETED
        //current j2 state:ACTIVE
        //j2: ended
        //post 1000ms
        //current j1 state:COMPLETED
        //current j2 state:COMPLETED
        //post 1200ms
        //current j1 state:COMPLETED
        //current j2 state:COMPLETED

    }

    fun testJobState2Crash(scope: CoroutineScope) = scope.launch {
        val handler = CoroutineExceptionHandler { _, e ->
            println("Caught by handler: ${e.message}")
        }

        launch(handler){
            supervisorScope {
                println("launched jobs")
                val j1 = launch() {
                    println("j1: started")
                    delay(500)
                    error("Something went wrong in j1")
                    println("j1: ended")
                }

                val j2 = launch() {
                    println("j2: started")
                    delay(1000)
                    println("j2: ended")
                }

                println("post 0ms")
                j1.state().let { println("current j1 state:$it") }
                j2.state().let { println("current j2 state:$it") }

                delay(250)
                println("post 250ms")
                j1.state().let { println("current j1 state:$it") }
                j2.state().let { println("current j2 state:$it") }

                delay(250)
                println("post 500ms")
                j1.state().let { println("current j1 state:$it") }
                j2.state().let { println("current j2 state:$it") }

                delay(250)
                println("post 750ms")
                j1.state().let { println("current j1 state:$it") }
                j2.state().let { println("current j2 state:$it") }

                delay(250)
                println("post 1000ms")
                j1.state().let { println("current j1 state:$it") }
                j2.state().let { println("current j2 state:$it") }


                delay(250)
                println("post 1200ms")
                j1.state().let { println("current j1 state:$it") }
                j2.state().let { println("current j2 state:$it") }

            }
        }
            //launched jobs
            //post 0ms
            //current j1 state:ACTIVE
            //current j2 state:ACTIVE
            //j1: started
            //j2: started
            //post 250ms
            //current j1 state:ACTIVE
            //current j2 state:ACTIVE
            //Caught by handler: Something went wrong in j1
            //post 500ms
            //current j1 state:CANCELLED
            //current j2 state:ACTIVE
            //post 750ms
            //current j1 state:CANCELLED
            //current j2 state:ACTIVE
            //j2: ended
            //post 1000ms
            //current j1 state:CANCELLED
            //current j2 state:COMPLETED
            //post 1200ms
            //current j1 state:CANCELLED
            //current j2 state:COMPLETED

    }

    fun testJobState3Cancel(scope: CoroutineScope){
        // based on the time instance of cancellation and the presence of next cooperative suspension point, a coroutine gets stopped from its execution
        scope.launch {
            val j1 = launch(Dispatchers.Default) {
                repeat(5){
                    println("j1 is working : $it")
                    delay(500)
                }
                delay(200)
                println("j1 finished work")
                delay(200)
                println("j1 says bye")

            }
            println("j1 state1: ${j1.state()}")
            delay(1300)//delay(2600)delay(2800)
            j1.cancel()
            println("j1 state2: ${j1.state()}")
            while(true) {
                delay(1000)
                j1.state().let { println("\ncurrent j1 state:$it\n") }
                println("\n[Main Scope] I am still alive")
            }
        }
    }


    fun testJobState4Zombie(scope: CoroutineScope){
        // this will forever print underscores even if we are trying to cancel it multiple times because
        // 1. j1 is not having any cooperative suspension points like delay/ensureactive/yield etc
        // 2. the parent scope is running forever
        // a coroutine gets cancelled on external cancellation signal or if its parent gets canceled
        scope.launch {
            val j1 = launch(Dispatchers.Default) {
                while (true){ print("_") }
            }
            println("\npost 0ms")
            j1.state().let { println("\ncurrent j1 state:$it\n") }
            delay(250)
            println("post 250ms")
            j1.state().let { println("\ncurrent j1 state:$it\n") }
            j1.cancel("cancel j1")
            delay(250)
            println("post 500ms")
            j1.state().let { println("\ncurrent j1 state:$it\n") }
            while(true) {
                delay(1000)
                j1.cancel()
                j1.state().let { println("\ncurrent j1 state:$it\n") }
                println("\n[Main Scope] I am still alive, and j1 should still be printing underscores...")
            }
        }
    }

    fun testJobState5GoodZombie(scope: CoroutineScope){
        // this will get cancelled because it is having a cooperative delay suspension point
        scope.launch {
            val j1 = launch(Dispatchers.Default) {
                while (true){
                    delay(50)
                    print("_")
                }
            }
            println("\npost 0ms")
            j1.state().let { println("\ncurrent j1 state:$it\n") }
            delay(250)
            println("post 250ms")
            j1.state().let { println("\ncurrent j1 state:$it\n") }
            j1.cancel("cancel j1")
            while(true) {
                delay(1000)
                j1.state().let { println("\ncurrent j1 state:$it\n") }
                println("\n[Main Scope] I am still alive")
            }
        }
    }


    fun Job.state(): String{
        return "isActive?$isActive | cancelled?$isCancelled | completed?$isCompleted"
    }





}