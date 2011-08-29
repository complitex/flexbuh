/****************************************************************/
/*                       ApplyRule(expr,pElement)               */
/****************************************************************/
function ApplyRule(ruleText, ruleKind, ruleDesc) {
    if (ruleKind == "fill") {
        expr = ruleText.replace(/SUM\(\^([A-Z]\w*)\)/g, "SUM('$1')");
        expr = expr.replace(/SUMF\(\$([^\$]*)\$\)/g, "SUMF(\\\"$1\\\")");
        expr = expr.replace(/(^\^([A-Z]\w*))/, "SetInputValue(ruleDesc,\"$2\",\"").replace(/=/, "");
        expr = expr.replace(/\^(\w*\.{0,1}\w*)/g, "(GetInputValue('$1'))");
        expr = expr.replace(/ABS/gi, "1*Math.abs");
        expr = expr.replace(/$/, "\")");
        eval(expr);

    }
    if (ruleKind == "check") {
        prEl = parElement.all(mainId);
        if (prEl != null) {
            pr = prEl.precision;
            if (pr != null && pr != 0)
                prc = ".toFixed(" + pr + ")";
            else
                prc = "";
            expr = "(" + controlList[i].getAttribute("c_doc_rowc") + controlList[i].getAttribute("sign").replace(/^=$/, ")==(") +
                    controlList[i].getAttribute("expression");
            expr = expr.replace(/SUM\(\^([A-Z]\w*)\)/g, "SUM('$1')");
            expr = expr.replace(/\^(\w*\.{0,1}\w*)/g, "(GetInputValue('$1'))");
            expr = expr.replace(/SUMF\(\$([^\$]*)\$\)/g, "SUMF(\"$1\")");
            expr = expr.replace(/ABS/gi, "1*Math.abs");
            expr += ")";

            expr = expr.replace(/==(.*)$/gi, "==!isFinite($1)?0:$1");
            expr = expr.replace(/==(.*)$/gi, "==(Math.round(1*($1)*Math.pow(10," + pr + "))/Math.pow(10," + pr + ")).toFixed(" + pr + ")");
            //alert(expr);
            if (!eval(expr)) {
                ErrorCount++;
                prEl.style.color = "red";
                ErrorMsg += "<li>" + controlList[i].getAttribute("description");
                if (controlList[i].attributes.getNamedItem("dodatok") != null)
                    ErrorMsg += "(<i>можливо додаток на даний момент не активний</i>)";
                ErrorMsg += (parElement.rownum != null ? "<i>(рядок " + parElement.rownum + " )</i>" : "");
                ErrorMsg += "</li>";
            }
            else {
                prEl.style.color = "black";
            }
        }
    }
}

/****************************************************************/
/*                SetInputValue(TargetId,expr)                  */
/****************************************************************/
function SetInputValue(desc, targetId, expr) {
    targetInput = parElement.all(targetId);
    if (targetInput != null) {
        if (AutoFillType == "") {
            newVal = eval(expr);
            newVal = ((targetInput.canBeX != "true" || (targetInput.canBeX == "true" && newVal != "X")) && isNaN(newVal) ? "" : newVal);
            newVal = (newVal == "Infinity" ? "" : newVal);
            targetInput.value = newVal;
            if (targetInput.canBeX != "true" || (targetInput.canBeX == "true" && newVal != "X"))
                targetInput.value = (targetInput.value != 0 ? new Number(Math.round(targetInput.value * Math.pow(10, 1 * targetInput.precision)) / Math.pow(10, 1 * targetInput.precision)).toFixed(1 * targetInput.precision) : "");
            if (targetInput.canBeX == "true") {
                if (targetInput.value == "X") {
                    targetInput.style.textAlign = "center";
                }
                else {
                    targetInput.style.textAlign = "right";
                }
            }
        }

        if (targetInput.style.backgroundColor != "royalblue" && desc != "" && targetInput.style.backgroundColor != "lightsteelblue")
            targetInput.style.backgroundColor = "thistle";
        targetInput.title = desc;
        targetInput.style.color = "black";
    }
}


/****************************************************************/
/*                GetInputValue (inputId)                       */
/****************************************************************/

function GetInputValue(inputId, mpl) {
    mpl = mpl == null ? 1 : 1 * mpl;
    inputId = inputId.replace(/\^/, "");

    if (/XXXX/.exec(inputId) == "XXXX") {//Теоретично таких випадків для заповнення з додатків не існує?
        fInput = parElement.all(inputId);
    }
    else {//Перевірка перехресних заповнень документів
        source = /^(\w*)\.(\w*)/.exec(inputId);
        if (source != null) {
            dodatokForm = GetDodatok(source[1]);
            fInput = (dodatokForm != null ? dodatokForm.all(source[2]) : null);
        }
        else {
            fInput = currentForm.all(inputId);
        }
    }

    if (fInput != null) {

        if (fInput.valueType !== "string" && fInput.valueType !== "date") {
            foundedValue = new Number((fInput.canBeX == "true" ? fInput.value.replace(/[xXхХ]{1,}/g, "") : fInput.value));
            foundedValue = (foundedValue == null ? 0 : foundedValue);
        }
        else
            foundedValue = fInput.value;
    }
    else
        foundedValue = 0;

    return foundedValue;
}
function GetDodatok(DocumentCode) {
    for (var j = 0; j < DocumentSet.children.length; j++) {
        var nextPageView = DocumentSet.children(j);
        var nextSet = nextPageView.all.currentSettings;
        if (GetDocumentType(nextSet) == DocumentCode) {
            return nextPageView.all.currentFormD;
        }
    }
    return null;
}

/****************************************************************/
/*               Sum()                                          */
/****************************************************************/
function SUM(ElementId) {
    curPageView = DocumentSet.children(DocumentSet.selectedIndex);
    curSet = curPageView.all.currentSettings;
    currentForm = curPageView.all.currentFormD;
    allElements = currentForm.all.namedItem(ElementId);

    if (allElements != null) {
        if (allElements.length == null) {
            return 1 * allElements.value.replace(/x/gi, "");
        }
        else {
            sum = 0;
            for (s = 0; s < allElements.length; s++) {
                cVal = allElements[s].value.replace(/x/gi, "");
                sum += (cVal == null ? 0 : 1 * cVal);
            }

            return sum;
        }
    }
    else {
        return 0;
    }
}
/**********************************************************************************
 *               SUMF(sExpr) Сума колонки таблиці з фільтром
 * sExpr-текстовий вираз формату:
 *  (GetInputValue('field_name'))=='const'?{знач.якщо.правда}:{знач.якщо.брех.}
 *  де:
 *   {знач.якщо.xxx} - може містити константу або (GetInputValue('field_name'))
 *********************************************************************************/
function SUMF(sExpr) {
    curPageView = DocumentSet.children(DocumentSet.selectedIndex);
    curSet = curPageView.all.currentSettings;
    currentForm = curPageView.all.currentFormD;
    result = 0;

//Отримання текстових виразів, які необхідно замінити виду (GetInputValue('field_name'))
    re = /\(GetInputValue\(\'\w+\'\)\)/gi;
    fields_expr = sExpr.match(re);
    if (fields_expr.length == null) {
        return(0);
    }
//Отримання відсіяних імен полів значення яких необхідно отримати, та створення масиву для зберіг.поточ.значень
    fields = new Array();
    fields = fields.concat(fields_expr);
    for (el in fields) {
        fields[el] = fields[el].replace(/\(GetInputValue\(\'/gi, "").replace(/\'\)\)/gi, "");
    }

// Отримуємо посилання на елемент вводу з ідентифікатором описаним в fields[0]
    allElements = currentForm.all.namedItem(fields[0]);
    if (allElements == null) {
        //елементів, що описують перше поле виразу не виявлено, виводимо повідомлення необхідне при відладці
        alert("Відладка:\r\n Вказаний елемент: " + fields[0] + " описаний у файлі контроля функції SUMF не " +
                "виявлений у документі");
        return(0);
    }
// Переводимо результат allElements, що може бути 1-значення, або масив у гарантований масив aElements
    var aElements = new Array();
    if (allElements.length == null) {
        aElements[0] = allElements;
    } else {
        for (var i = 0; i < allElements.length; i++)
            aElements[i] = allElements(i);
    }
// Проводимо перебор елементів INPUT, що відповідають 1-му ідентифікатору та знаходимо інші ідентифікатори даної строки таблиці
    for (nrow in aElements) {

// Знаходимо посилання на строку таблиці, що містить поточні введені дані строки
        for (var i in aElements) {
            row = aElements[nrow];
            while (row.tagName != 'TR')
                row = row.parentElement;
        }
        //для імен полів визначаємо їх значення у стр.та у виразі,
        //що переданий на обчислення замінюємо імена полів на значення
        runExpr = sExpr;
        for (var i in fields) {
            fields_value = row.all.item(fields[i]) == null ? "0" :
                    (row.all.item(fields[i]).value == null ? "0" : "'" + row.all.item(fields[i]).value) + "'";
            runExpr = runExpr.replace(fields_expr[i], fields_value);
        }
        //обчислення виразу
        result += 1 * eval(runExpr);
    }
    return(result);

}