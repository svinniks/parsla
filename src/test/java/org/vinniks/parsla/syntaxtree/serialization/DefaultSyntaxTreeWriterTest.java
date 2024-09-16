package org.vinniks.parsla.syntaxtree.serialization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.vinniks.parsla.syntaxtree.SyntaxTreeBuilder;
import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;
import org.vinniks.parsla.tokenizer.text.TextPosition;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultSyntaxTreeWriterTest {
    @Test
    void shouldCreateWriterWithNoArgumentConstructor() {
        var syntaxTreeWriter = new DefaultSyntaxTreeWriter();

        assertThat(syntaxTreeWriter.isWritePositions()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldCreateWriterWithWritePositionsArgument(boolean writePositions) {
        var syntaxTreeWriter = new DefaultSyntaxTreeWriter(writePositions);

        assertThat(syntaxTreeWriter.isWritePositions()).isEqualTo(writePositions);
    }

    @Test
    void shouldWriteSyntaxTreeOfOneNodeWithoutPosition() throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(new TextPosition(1, 1), "value-1");
        var syntaxTree = syntaxTreeBuilder.build();
        var writtenSyntaxTree = writeSyntaxTree(syntaxTree, false);

        assertThat(writtenSyntaxTree).isEqualTo("""
            value-1
            """);
    }

    @Test
    void shouldWriteSyntaxTreeOfOneNodeWithPosition() throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(new TextPosition(1, 1), "value-1");
        var syntaxTree = syntaxTreeBuilder.build();
        var writtenSyntaxTree = writeSyntaxTree(syntaxTree, true);

        assertThat(writtenSyntaxTree).isEqualTo("""
            value-1 at 1:1
            """);
    }

    @Test
    void shouldWriteSyntaxTreeOfOneNodeWithNullPosition() throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        var syntaxTree = syntaxTreeBuilder.build();
        var writtenSyntaxTree = writeSyntaxTree(syntaxTree, true);

        assertThat(writtenSyntaxTree).isEqualTo("""
            value-1
            """);
    }

    @Test
    void shouldWriteSyntaxTreeOfMultipleNodesWithoutPosition() throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(new TextPosition(1, 1), "value-1");
        syntaxTreeBuilder.enter(new TextPosition(1, 2), "value-1-1");
        syntaxTreeBuilder.tap(new TextPosition(1, 3), "value-1-1-1");
        syntaxTreeBuilder.tap(new TextPosition(1, 4), "value-1-1-2");
        syntaxTreeBuilder.exit();
        syntaxTreeBuilder.enter(new TextPosition(2, 1), "value-1-2");
        syntaxTreeBuilder.exit();
        var syntaxTree = syntaxTreeBuilder.build();
        var writtenSyntaxTree = writeSyntaxTree(syntaxTree, false);

        assertThat(writtenSyntaxTree).isEqualTo("""
            value-1
                value-1-1
                    value-1-1-1
                    value-1-1-2
                value-1-2
            """);
    }

    @Test
    void shouldWriteSyntaxTreeOfMultipleNodesWithPosition() throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(new TextPosition(1, 1), "value-1");
        syntaxTreeBuilder.enter(new TextPosition(1, 2), "value-1-1");
        syntaxTreeBuilder.tap(new TextPosition(1, 3), "value-1-1-1");
        syntaxTreeBuilder.tap(new TextPosition(1, 4), "value-1-1-2");
        syntaxTreeBuilder.exit();
        syntaxTreeBuilder.enter(new TextPosition(2, 1), "value-1-2");
        syntaxTreeBuilder.exit();
        var syntaxTree = syntaxTreeBuilder.build();
        var writtenSyntaxTree = writeSyntaxTree(syntaxTree, true);

        assertThat(writtenSyntaxTree).isEqualTo("""
            value-1 at 1:1
                value-1-1 at 1:2
                    value-1-1-1 at 1:3
                    value-1-1-2 at 1:4
                value-1-2 at 2:1
            """);
    }

    private String writeSyntaxTree(SyntaxTreeNode<TextPosition> syntaxTree, boolean writePositions) throws IOException {
        var writer = new StringWriter();
        var syntaxTreeWriter = new DefaultSyntaxTreeWriter(writePositions);
        syntaxTreeWriter.write(syntaxTree, writer);
        return writer.toString();
    }
}