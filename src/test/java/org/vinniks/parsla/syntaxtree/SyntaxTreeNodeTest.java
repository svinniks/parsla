package org.vinniks.parsla.syntaxtree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vinniks.parsla.exception.SyntaxTreeException;
import org.vinniks.parsla.tokenizer.text.TextPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class SyntaxTreeNodeTest {
    @Test
    void shouldCreateSyntaxTreeNode() {
        var position = new TextPosition(1, 2);
        List<SyntaxTreeNode<Object>> children = new ArrayList<>();

        var syntaxTreeNode = new SyntaxTreeNode<>(position, "value-1", children);

        assertThat(syntaxTreeNode.value()).isEqualTo("value-1");
        assertThat(syntaxTreeNode.position()).isSameAs(position);
        assertThat(syntaxTreeNode.children()).isSameAs(children);
    }

    @Test
    void shouldConvertSyntaxTreeNodeToString() {
        var syntaxTreeNode = new SyntaxTreeNode<>(new TextPosition(1, 2), "value-1", Collections.emptyList());

        assertThat(syntaxTreeNode.toString()).isEqualTo("""
            value-1 at 1:2
            """);
    }

    @Test
    void shouldReturnTrueWhileCheckingValueIsWhenValueMatches() {
        var syntaxTreeNode = new SyntaxTreeNode<>(null, "value-1", Collections.emptyList());

        assertThat(syntaxTreeNode.valueIs("value-1")).isTrue();
    }

    @Test
    void shouldReturnTrueWhileCheckingValueIsWhenValueDoesNotMatch() {
        var syntaxTreeNode = new SyntaxTreeNode<>(null, "value-1", Collections.emptyList());

        assertThat(syntaxTreeNode.valueIs("value-1")).isTrue();
    }

    @Test
    void shouldThrowSyntaxTreeExceptionWhileGettingOptionalChildWhenMultipleChildrenExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreNode = syntaxTreeBuilder.build();

        assertThatThrownBy(() -> syntaxTreNode.optionalChild("value-1-1"))
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 has multiple value-1-1");
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingOptionalChildWhenChildDoesNotExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreNode.optionalChild("value-1-2")).isEmpty();
    }

    @Test
    void shouldReturnOptionalWithCorrectChildWhileGettingOptionalChildWhenChildExists() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-2");
        syntaxTreeBuilder.tap(null, "value-1-3");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreeNode.optionalChild("value-1-2")).hasValue(syntaxTreeNode.children().get(1));
    }

    @Test
    void shouldThrowSyntaxTreeExceptionWhileCheckingIsChildWhenMultipleChildrenExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreNode = syntaxTreeBuilder.build();

        assertThatThrownBy(() -> syntaxTreNode.hasChild("value-1-1"))
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 has multiple value-1-1");
    }

    @Test
    void shouldReturnFalseWhileCheckingHasChildWhenChildDoesNotExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreNode.hasChild("value-1-2")).isFalse();
    }

    @Test
    void shouldReturnTrueWhileCheckingHasChildWhenChildExists() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-2");
        syntaxTreeBuilder.tap(null, "value-1-3");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreeNode.hasChild("value-1-2")).isTrue();
    }

    @Test
    void shouldThrowSyntaxTreeExceptionWhileGettingChildWhenMultipleChildrenExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreNode = syntaxTreeBuilder.build();

        assertThatThrownBy(() -> syntaxTreNode.child("value-1-1"))
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 has multiple value-1-1");
    }

    @Test
    void shouldThrowSyntaxTreeExceptionWhileGettingChildWhenChildNotExists() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreNode = syntaxTreeBuilder.build();

        assertThatThrownBy(() -> syntaxTreNode.child("value-1-2"))
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 does not contain value-1-2");
    }

    @Test
    void shouldReturnChildWhileGettingChildWhenChildExists() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-2");
        syntaxTreeBuilder.tap(null, "value-1-3");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreeNode.child("value-1-2")).isSameAs(syntaxTreeNode.children().get(1));
    }

    @Test
    void shouldThrowSyntaxTreeExceptionWhileGettingOptionalOnlyChildWhileMultipleChildrenExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-2");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThatThrownBy(syntaxTreeNode::optionalChild)
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 has multiple children");
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingOptionalOnlyChildWhenNodeHasNoChildren() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreeNode.optionalChild()).isEmpty();
    }

    @Test
    void shouldReturnOnlyChildWhileGettingOptionalOnlyChildWhenNodeHasOneChild() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreeNode.optionalChild()).hasValue(syntaxTreeNode.children().get(0));
    }

    @Test
    void shouldThrowSyntaxTreeExceptionWhileGettingOnlyChildWhileMultipleChildrenExist() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        syntaxTreeBuilder.tap(null, "value-1-2");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThatThrownBy(syntaxTreeNode::child)
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 has multiple children");
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingOnlyChildWhenNodeHasNoChildren() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThatThrownBy(syntaxTreeNode::child)
            .isInstanceOf(SyntaxTreeException.class)
            .hasMessage("value-1 does not have children");
    }

    @Test
    void shouldReturnOnlyChildWhileGettingOnlyChildWhenNodeHasOneChild() {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        syntaxTreeBuilder.enter(null, "value-1");
        syntaxTreeBuilder.tap(null, "value-1-1");
        var syntaxTreeNode = syntaxTreeBuilder.build();

        assertThat(syntaxTreeNode.child()).isSameAs(syntaxTreeNode.children().get(0));
    }

    @Test
    void shouldGetOptionalChildValue() {
        var childNode = new SyntaxTreeNode<>(null, "value-1-1", Collections.emptyList());
        var containerNode = spy(new SyntaxTreeNode<>(null, "value-1", Collections.emptyList()));
        doReturn(Optional.of(childNode)).when(containerNode).optionalChild();
        doAnswer(Answers.CALLS_REAL_METHODS).when(containerNode).optionalChildValue();

        var returnedValue = containerNode.optionalChildValue();

        assertThat(returnedValue).hasValue("value-1-1");
    }

    @Test
    void shouldGetChildValue() {
        var childNode = new SyntaxTreeNode<>(null, "value-1-1", Collections.emptyList());
        var containerNode = spy(new SyntaxTreeNode<>(null, "value-1", Collections.emptyList()));
        doReturn(childNode).when(containerNode).child();
        doAnswer(Answers.CALLS_REAL_METHODS).when(containerNode).childValue();

        var returnedValue = containerNode.childValue();

        assertThat(returnedValue).isEqualTo("value-1-1");
    }

    @Test
    void shouldGetOptionalSingular() {
        var childChildNode = new SyntaxTreeNode<>(null, "value-1-1", Collections.emptyList());
        var childNode = mock(SyntaxTreeNode.class);
        doReturn(childChildNode).when(childNode).child();
        var containerNode = spy(new SyntaxTreeNode<>(null, "value-1", Collections.emptyList()));
        doReturn(Optional.of(childNode)).when(containerNode).optionalChild("value-1");
        doAnswer(Answers.CALLS_REAL_METHODS).when(containerNode).optionalSingular("value-1");

        var returnedValue = containerNode.optionalSingular("value-1");

        assertThat(returnedValue).hasValue("value-1-1");
    }

    @Test
    void shouldGetSingular() {
        var childNode = mock(SyntaxTreeNode.class);
        doReturn("value-1-1").when(childNode).childValue();
        var containerNode = spy(new SyntaxTreeNode<>(null, "value-1", Collections.emptyList()));
        doReturn(childNode).when(containerNode).child("value-1");
        doAnswer(Answers.CALLS_REAL_METHODS).when(containerNode).singular("value-1");

        var returnedValue = containerNode.singular("value-1");

        assertThat(returnedValue).isEqualTo("value-1-1");
    }
}