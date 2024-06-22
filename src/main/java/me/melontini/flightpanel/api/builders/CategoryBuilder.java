package me.melontini.flightpanel.api.builders;

import com.google.common.collect.ImmutableList;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CategoryBuilder implements List<BaseElementBuilder<?, ?, ?>> {

    private final Text title;
    private final List<BaseElementBuilder<?, ?, ?>> list = new ArrayList<>();

    public CategoryBuilder(Text text) {
        this.title = text;
    }

    public CategoryBuilder addElement(BaseElementBuilder<?, ?, ?> abstractConfigElement) {
        this.list.add(abstractConfigElement);
        return this;
    }

    public Text title() {
        return this.title;
    }

    public List<BaseElementBuilder<?, ?, ?>> build() {
        return ImmutableList.copyOf(this.list);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @NotNull @Override
    public Iterator<BaseElementBuilder<?, ?, ?>> iterator() {
        return this.list.iterator();
    }

    @NotNull @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @NotNull @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return this.list.toArray(a);
    }

    @Override
    public boolean add(BaseElementBuilder<?, ?, ?> abstractConfigElement) {
        return this.list.add(abstractConfigElement);
    }

    @Override
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends BaseElementBuilder<?, ?, ?>> c) {
        return this.list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends BaseElementBuilder<?, ?, ?>> c) {
        return this.list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public BaseElementBuilder<?, ?, ?> get(int index) {
        return this.list.get(index);
    }

    @Override
    public BaseElementBuilder<?, ?, ?> set(int index, BaseElementBuilder<?, ?, ?> element) {
        return this.list.set(index, element);
    }

    @Override
    public void add(int index, BaseElementBuilder<?, ?, ?> element) {
        this.list.add(index, element);
    }

    @Override
    public BaseElementBuilder<?, ?, ?> remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @NotNull @Override
    public ListIterator<BaseElementBuilder<?, ?, ?>> listIterator() {
        return this.list.listIterator();
    }

    @NotNull @Override
    public ListIterator<BaseElementBuilder<?, ?, ?>> listIterator(int index) {
        return this.list.listIterator(index);
    }

    @NotNull @Override
    public List<BaseElementBuilder<?, ?, ?>> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }
}
