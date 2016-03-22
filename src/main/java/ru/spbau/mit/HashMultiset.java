package ru.spbau.mit;

import java.util.*;

public class HashMultiset<E> implements Multiset<E> {
    private LinkedHashMap<E, Integer> map = new LinkedHashMap<>();

    private class Entry<E> implements Multiset.Entry<E> {
        private Map.Entry<E, Integer> entry;

        Entry(Map.Entry<E, Integer> entry) {
            this.entry = entry;
        }

        @Override
        public E getElement() {
            return entry.getKey();
        }

        @Override
        public int getCount() {
            return entry.getValue();
        }
    }

    @Override
    public int count(Object element) {
        if (map.containsKey(element)) {
            return map.get(element);
        }
        return 0;
    }

    @Override
    public Set<E> elementSet() {
        return map.keySet();
    }

    @Override
    public Set<? extends Entry<E>> entrySet() {
        Set<HashMultiset<E>.Entry<E>> entries = new LinkedHashSet<>();

        for (Map.Entry<E, Integer> e : map.entrySet()) {
            entries.add(new HashMultiset<E>.Entry<E>(e));
        }
        return entries;
    }

    @Override
    public int size() {
        int s = 0;
        for (Integer c : map.values()) {
            s += c;
        }
        return s;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new HashMultisetIterator();
    }

    private class HashMultisetIterator implements Iterator<E> {
        Iterator<Map.Entry<E, Integer>> entryIterator;
        Map.Entry<E, Integer> currentEntry;
        int repeatsLeft;
        boolean willRemoveFirstTime;

        HashMultisetIterator() {
            this.entryIterator = map.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return repeatsLeft > 0 || entryIterator.hasNext();
        }

        @Override
        public E next() {
            if (repeatsLeft == 0) {
                currentEntry = entryIterator.next();
                repeatsLeft = currentEntry.getValue();
            }
            repeatsLeft--;
            willRemoveFirstTime = true;
            return currentEntry.getKey();
        }

        @Override
        public void remove() {
            if (!willRemoveFirstTime) {
                throw new IllegalStateException();
            }
            if (currentEntry.getValue() <= 0) {
                throw new ConcurrentModificationException();
            }
            currentEntry.setValue(currentEntry.getValue() - 1);
            if (currentEntry.getValue() == 0) {
                entryIterator.remove();
            }
            willRemoveFirstTime = false;
        }
    }

    @Override
    public Object[] toArray() {
        Iterator<E> itr = this.iterator();
        Object[] array = new Object[this.size()];
        int k = 0;
        while (itr.hasNext()) {
            array[k] = itr.next();
            k += 1;
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        if (map.containsKey(e)) {
            assert map.get(e) > 0;
            map.put(e, map.get(e) + 1);
        } else {
            map.put(e, 1);
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (map.containsKey(o)) {
            if (map.get(o) == 0) {
                map.remove(o);
            } else {
                map.put((E) o, map.get(o) - 1);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
