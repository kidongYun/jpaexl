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

- implements findAll() method (complete)
- change findAll() method to get list. (complete)
- resolve the error occurred when save() method are called several. (complete)
- create Asserts object.
- do condition processing whether the same id tuples are already existed in Excel.
- get annotation from Excel

# 2022.2.26

- resolve null issue when you call save message. (complete)
- if some items what have same id with the one that is already existed in excel, then it should be not added in there.
  - implements isIdentifier() method (complete)
  - implements findCursor() method

## 2022.03.02

- create the instantiation class for building instance of any types like String, Long, LocalDate ... etc (complete)
- refactor Table interface that it changes id type from String to Data<?> (complete)

## 2022.03.04

- it should be saved without duplicated id.

## TODO

- when schema is changed after it is already created, the schema in excel should be updated.
- cache Schema in persistanceManager. File I/O for searching it is too occurred frequently. 