package com.lovelycatv.ai.crystalapp.test

import com.lovelycatv.ai.crystalapp.data.BranchPath
import com.lovelycatv.ai.crystalapp.data.ChatHistoryMessage
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertTrue

/**
 * @author lovelycat
 * @since 2025-04-20 18:18
 * @version 1.0
 */
class ChatHistoryMessageTreeTests {
    /**
     * 1 -- 2 -- 3 -- 4
     */
    private val listTree = TestTree(1).apply {
        addChildNode(TestTree(2).apply {
            addChildNode(TestTree(3).apply {
                addChildNode(TestTree(4))
            })
        })
    }

    /**
     *    1
     *  2   3
     *  4   5
     *  6
     */
    private val simpleBranchTree = TestTree(1).apply {
        addChildNode(TestTree(2).apply {
            addChildNode(TestTree(4).apply {
                addChildNode(TestTree(6))
            })
        })
        addChildNode(TestTree(3).apply {
            addChildNode(TestTree(5))
        })
    }

    /**
     *          1
     *          2
     *     3         4
     *  5            6
     *           7      8
     *       9  10  11
     *       12
     *       13
     */
    private val complexTree = TestTree(1).apply {
        addChildNode(TestTree(2).apply {
            addChildNode(TestTree(3).apply {
                addChildNode(TestTree(5))
            })
            addChildNode(TestTree(4).apply {
                addChildNode(TestTree(6).apply {
                    addChildNode(TestTree(7).apply {
                        addChildNode(TestTree(9).apply {
                            addChildNode(TestTree(12).apply {
                                addChildNode(TestTree(13))
                            })
                        })
                        addChildNode(TestTree(10))
                        addChildNode(TestTree(11))
                    })
                    addChildNode(TestTree(8))
                })
            })
        })
    }

    @Test
    fun leafPathsTest() {
        val v1 = listTree.findAllLeafPaths().map { it.toString() }.also { println(it) }
        val v1Paths = listOf("[0, 0, 0]")
        assertTrue(v1.containsAll(v1Paths) && v1.size == v1Paths.size, "Expected: $v1Paths")

        val v2 = simpleBranchTree.findAllLeafPaths().map { it.toString() }.also { println(it) }
        val v2Paths = listOf("[0, 0, 0]", "[1, 0]")
        assertTrue(v2.containsAll(v2Paths) && v2.size == v2Paths.size, "Expected: $v2Paths")

        val v3 = complexTree.findAllLeafPaths().map { it.toString() }.also { println(it) }
        val v3Paths = listOf("[0, 0, 0]", "[0, 1, 0, 0, 0, 0, 0]", "[0, 1, 0, 0, 1]", "[0, 1, 0, 0, 2]", "[0, 1, 0, 1]")
        assertTrue(v3.containsAll(v3Paths) && v3.size == v3Paths.size, "Expected: $v3Paths")
    }

    @Test
    fun findChainByPath() {
        val v1 = listTree.getMessageChainByBranchPath(BranchPath("0, 0, 0")).map { it.id }.also { println(it) }
        assert(v1.toString() == "[1, 2, 3, 4]")

        val v2 = simpleBranchTree.getMessageChainByBranchPath(BranchPath("0, 0, 0")).map { it.id }.also { println(it) }
        assert(v2.toString() == "[1, 2, 4, 6]")

        val v3 = complexTree.getMessageChainByBranchPath(BranchPath("0, 1, 0, 0, 0, 0, 0")).map { it.id }.also { println(it) }
        assert(v3.toString() == "[1, 2, 4, 6, 7, 9, 12, 13]")
    }

    @Test
    fun findLeafByPath() {
        val v1 = listTree.getMessageChainByBranchPath(BranchPath("0, 0, 0"), true).map { it.id }.also { println(it) }
        assert(v1.toString() == "[4]")

        val v2 = simpleBranchTree.getMessageChainByBranchPath(BranchPath("0, 0, 0"), true).map { it.id }.also { println(it) }
        assert(v2.toString() == "[6]")

        val v3 = complexTree.getMessageChainByBranchPath(BranchPath("0, 1, 0, 0, 0, 0, 0"), true).map { it.id }.also { println(it) }
        assert(v3.toString() == "[13]")
    }

    /**
     *           1
     *           2
     *      3         4
     *   5            6
     *           7         8
     *       9  10 (11)
     *       12
     *      (13)
     */
    private val complexTree2 = TestTree(1).apply {
        addChildNode(TestTree(2).apply {
            addChildNode(TestTree(3).apply {
                addChildNode(TestTree(5))
            })
            addChildNode(TestTree(4).apply {
                addChildNode(TestTree(6).apply {
                    addChildNode(TestTree(7).apply {
                        addChildNode(TestTree(9).apply {
                            addChildNode(TestTree(12).apply {
                                addChildNode(TestTree(13, false))
                            })
                        })
                        addChildNode(TestTree(10))
                        addChildNode(TestTree(11, false))
                    })
                    addChildNode(TestTree(8))
                })
            })
        })
    }

    /**
     *            1
     *            2
     *     (3)          4
     *   5             (6)
     *           (7)        (8)
     *       9  (10) (11)
     *       12
     *      (13)
     */
    private val complexTree3 = TestTree(1).apply {
        addChildNode(TestTree(2).apply {
            addChildNode(TestTree(3, false).apply {
                addChildNode(TestTree(5))
            })
            addChildNode(TestTree(4).apply {
                addChildNode(TestTree(6, false).apply {
                    addChildNode(TestTree(7, false).apply {
                        addChildNode(TestTree(9).apply {
                            addChildNode(TestTree(12).apply {
                                addChildNode(TestTree(13, false))
                            })
                        })
                        addChildNode(TestTree(10, false))
                        addChildNode(TestTree(11, false))
                    })
                    addChildNode(TestTree(8, false))
                })
            })
        })
    }

    @Test
    fun findAvailablePaths() {
        val answers1 = complexTree2.findAllAvailableLeaves(TestTree.nodeFilter).map { it.map { it.id }.toString() }.also { println(it) }
        val correctAnswers1 = listOf("[1, 2, 3, 5]", "[1, 2, 4, 6, 7, 10]", "[1, 2, 4, 6, 8]", "[1, 2, 4, 6, 7, 9, 12]")
        assertTrue(answers1.containsAll(correctAnswers1) && answers1.size == correctAnswers1.size)

        val answers2 = complexTree3.findAllAvailableLeaves(TestTree.nodeFilter).map { it.map { it.id }.toString() }.also { println(it) }
        val correctAnswers2 = listOf("[1, 2, 5]", "[1, 2, 4, 9, 12]")
        assertTrue(answers2.containsAll(correctAnswers2) && answers2.size == correctAnswers2.size)
    }

    data class TestTree(val id: Int, val available: Boolean = true) : ChatHistoryMessage<TestTree> {
        companion object {
            val nodeFilter = object : ChatHistoryMessage.NodeFilter<TestTree> {
                override fun getUniqueId(t: TestTree): Any {
                    return t.id
                }

                override fun isAvailable(t: TestTree): Boolean {
                    return t.available
                }

            }
        }

        private val _children: MutableList<TestTree> = mutableListOf()
        override val children: List<TestTree> = this._children

        /**
         * Get the sub-type instance of [ChatHistoryMessage]
         *
         * @return Real instance of [ChatHistoryMessage]
         */
        override fun getMessageEntity(): TestTree {
            return this
        }

        override fun addChildNodes(children: Collection<TestTree>) {
            this._children.addAll(children)
        }

        override fun addChildNode(child: TestTree) {
            this._children.add(child)
        }
    }
}