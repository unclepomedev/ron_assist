package com.github.unclepomedev.ronassist.editor.structureview

import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonStructureViewTest : BasePlatformTestCase() {

    fun testMapEntries() = assertTree(
        """
            {
                "name": "Reimu",
                "score": 100,
            }
        """.trimIndent(),
        """
            -RON File
             -{2}
              "name"
              "score"
        """.trimIndent(),
    )

    fun testNamedStruct() = assertTree(
        """
            Player(
                name: "Reimu",
                hp: 100,
            )
        """.trimIndent(),
        """
            -RON File
             -Player(...)
              name
              hp
        """.trimIndent(),
    )

    fun testList() = assertTree(
        """
            [1, 2, 3]
        """.trimIndent(),
        """
            -RON File
             [3]
        """.trimIndent(),
    )

    fun testAnonymousTupleAllPrimitives() = assertTree(
        """
            ("first", "second", "third")
        """.trimIndent(),
        """
            -RON File
             (...)
        """.trimIndent(),
    )

    fun testPrimitiveRoot() = assertTree(
        """
            42
        """.trimIndent(),
        """
            RON File
        """.trimIndent(),
    )

    fun testEmptyMap() = assertTree(
        """
            {}
        """.trimIndent(),
        """
            -RON File
             {0}
        """.trimIndent(),
    )

    fun testNestedMap() = assertTree(
        """
            {
                "outer": {
                    "inner": "value",
                },
            }
        """.trimIndent(),
        """
            -RON File
             -{1}
              -"outer"
               -{1}
                "inner"
        """.trimIndent(),
    )

    fun testMixedNesting() = assertTree(
        """
            Game(
                title: "Touhou",
                players: [
                    Player(name: "Reimu"),
                    Player(name: "Marisa"),
                ],
                config: {
                    "volume": 80,
                },
            )
        """.trimIndent(),
        """
            -RON File
             -Game(...)
              title
              -players
               -[2]
                -Player(...)
                 name
                -Player(...)
                 name
              -config
               -{1}
                "volume"
        """.trimIndent(),
    )

    fun testListOfMaps() = assertTree(
        """
            [
                {"a": 1},
                {"b": 2},
            ]
        """.trimIndent(),
        """
            -RON File
             -[2]
              -{1}
               "a"
              -{1}
               "b"
        """.trimIndent(),
    )

    fun testAnonymousTupleWithContainer() = assertTree(
        """
            Pair(
                nested: ("a", {"b": "c"}),
            )
        """.trimIndent(),
        """
            -RON File
             -Pair(...)
              -nested
               -(...)
                -{1}
                 "b"
        """.trimIndent(),
    )

    fun testLeadingComment() = assertTree(
        """
            // header comment
            {
                "a": 1,
            }
        """.trimIndent(),
        """
            -RON File
             -{1}
              "a"
        """.trimIndent(),
    )

    fun testMapEntryWithSpacesInKey() = assertTree(
        """
            {
                "key with spaces": "value",
            }
        """.trimIndent(),
        """
            -RON File
             -{1}
              "key with spaces"
        """.trimIndent(),
    )

    private fun assertTree(content: String, expected: String) {
        myFixture.configureByText("test.ron", content)
        myFixture.testStructureView { view ->
            PlatformTestUtil.expandAll(view.tree)
            PlatformTestUtil.assertTreeEqual(view.tree, expected)
        }
    }
}