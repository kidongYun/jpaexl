## 2021.1.31

- resolve the issue that schema.type returns null (complete)
- implement find function
- re-implement about row cursor

## 2022.2.4

- Implement findById (complete)

## 2022.2.8

- Add annotation type in excel when the data is saved

## 2022.2.9

- refactoring persistenceManager, table
  - findData, insertValue move from PersistenceManager to Table. 
  - Use Sheet
- Add annotation type in excel when the data is saved
- handle exception jpaexl.xlsx FileNotFoundException
- resolve IndexOutOfBoundsException

## 2022.2.10

- resolve IndexOutOfBoundsException (complete)
  - what is difference between the below two codes.
```java
class SimpleTable {
  private void insertValue(String value, int rowCur, int cellCur) {
    Row row = sheet.getRow(rowCur);

    if (Objects.isNull(row)) {
      row = sheet.createRow(rowCur);
    }

    row.createCell(cellCur).setCellValue(value);
  }
}
```

```java
class SimpleTable {
  private void insertValue(String value, int rowCur, int cellCur) {
      Optional.ofNullable(sheet.getRow(rowCur)).orElse(sheet.createRow(rowCur)).createCell(cellCur).setCellValue(value);
  }
}
```

## 2022.2.12

- use stream.takewhile() into findCellSize()
- implements findAll() method (complete)
- change findAll() method to get list.
- resolve the error occurred when save() method are called several.
- create Asserts object.