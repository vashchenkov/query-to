package ru.gubber.queryto.model;

/**
 * Объект, отражающий сортировку списка
 * Created by gubber on 29.04.2015.
 */
public class SorterTO {
    /**
     * Данное поле опосредовано указывает на поле, по которому сортировать, чтобы не светить названия реальных полей в сети.
     */
    private String sorterName;
    /**
     * Направление сортировки: 0 - ASC, сортирвка по возрастанию, любое другое значение - DESC, по убыванию.
     */
    private int sortOrder;

    public String getSorterName() {
        return sorterName;
    }

    public void setSorterName(String sorterName) {
        this.sorterName = sorterName;
    }

    /**
     * Данный метод не надо использовать в коде, чтобы исключить момент ошибки.
     * @return
     */
    @Deprecated
    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Данный метод возвращает ложь или истину, в зависимости от того, какой тип сортировки задан
     * @return
     */
    public boolean isAscending(){
        return sortOrder == 0;
    }
}