# ==================== STACK IMPLEMENTATION (Array-based) ====================

class Stack:
    def __init__(self):
        self._data = []

    def push(self, item):
        self._data.append(item)

    def pop(self):
        if self.is_empty():
            raise IndexError("Stack Underflow")
        return self._data.pop()

    def peek(self):
        if self.is_empty():
            raise IndexError("Stack is Empty")
        return self._data[-1]

    def is_empty(self):
        return len(self._data) == 0

    def __str__(self):
        return str(self._data)

    def __repr__(self):
        return self.__str__()


# ==================== HELPER FUNCTIONS ====================

def precedence(op: str) -> int:
    if op in ('+', '-'):
        return 1
    if op in ('*', '/'):
        return 2
    if op == '^':
        return 3
    return 0


def is_operator(c: str) -> bool:
    return c in ('+', '-', '*', '/', '^')


def apply_op(a: float, b: float, op: str) -> float:
    if op == '+': return a + b
    if op == '-': return a - b
    if op == '*': return a * b
    if op == '/':
        if b == 0:
            raise ZeroDivisionError("Pembagian dengan nol!")
        return a / b
    if op == '^': return a ** b
    raise ValueError(f"Operator tidak dikenal: {op}")


def format_num(n: float) -> str:
    if n == int(n):
        return str(int(n))
    return f"{n:.4f}"


def tokenize(expr: str) -> list:
    """Tokenize infix expression into list of tokens."""
    tokens = []
    i = 0
    while i < len(expr):
        c = expr[i]
        if c == ' ':
            i += 1
            continue
        if c.isdigit() or c == '.':
            num = ''
            while i < len(expr) and (expr[i].isdigit() or expr[i] == '.'):
                num += expr[i]
                i += 1
            tokens.append(num)
        else:
            tokens.append(c)
            i += 1
    return tokens


# ==================== INFIX TO POSTFIX (Shunting-Yard) ====================

def infix_to_postfix(infix: str) -> str:
    print("\n╔══════════════════════════════════════════════════════════════╗")
    print("║           KONVERSI INFIX → POSTFIX (Shunting-Yard)          ║")
    print("╚══════════════════════════════════════════════════════════════╝")
    print(f"{'Step':<5} {'Token':<12} {'Stack Operator':<25} {'Output (Postfix)':<20}")
    print("─" * 65)

    op_stack = Stack()
    output = []
    tokens = tokenize(infix)
    step = 1

    for token in tokens:
        if token == ' ':
            continue

        # Operand (angka)
        try:
            float(token)
            output.append(token)
            print(f"{step:<5} {token:<12} {str(op_stack._data):<25} {' '.join(output):<20}")
            step += 1
            continue
        except ValueError:
            pass

        # Kurung buka
        if token == '(':
            op_stack.push(token)
            print(f"{step:<5} {'(':<12} {str(op_stack._data):<25} {' '.join(output):<20}")
            step += 1

        # Kurung tutup
        elif token == ')':
            while not op_stack.is_empty() and op_stack.peek() != '(':
                output.append(op_stack.pop())
            if not op_stack.is_empty():
                op_stack.pop()  # buang '('
            print(f"{step:<5} {')':<12} {str(op_stack._data):<25} {' '.join(output):<20}")
            step += 1

        # Operator
        elif is_operator(token):
            # ^ bersifat right-associative
            while (not op_stack.is_empty() and
                   op_stack.peek() != '(' and
                   (precedence(op_stack.peek()) > precedence(token) or
                    (precedence(op_stack.peek()) == precedence(token) and token != '^'))):
                output.append(op_stack.pop())
            op_stack.push(token)
            print(f"{step:<5} {token:<12} {str(op_stack._data):<25} {' '.join(output):<20}")
            step += 1

    # Keluarkan sisa operator
    while not op_stack.is_empty():
        output.append(op_stack.pop())
        print(f"{step:<5} {'(flush)':<12} {str(op_stack._data):<25} {' '.join(output):<20}")
        step += 1

    print("─" * 65)
    postfix = ' '.join(output)
    print(f"✅ Postfix: {postfix}")
    return postfix


# ==================== EVALUATE POSTFIX ====================

def evaluate_postfix(postfix: str) -> float:
    print("\n╔══════════════════════════════════════════════════════════════╗")
    print("║          EVALUASI POSTFIX (Step-by-Step Stack)               ║")
    print("╚══════════════════════════════════════════════════════════════╝")
    print(f"{'Step':<5} {'Token':<12} {'Aksi':<10} {'Stack Operand':<30} {'Keterangan':<25}")
    print("─" * 85)

    val_stack = Stack()
    tokens = postfix.split()
    step = 1

    for token in tokens:
        if not token:
            continue

        try:
            num = float(token)
            val_stack.push(num)
            stack_display = str([format_num(x) for x in val_stack._data])
            print(f"{step:<5} {token:<12} {'PUSH':<10} {stack_display:<30} Push {format_num(num)}")
            step += 1

        except ValueError:
            if is_operator(token):
                b = val_stack.pop()
                a = val_stack.pop()
                result = apply_op(a, b, token)
                val_stack.push(result)
                keterangan = f"{format_num(a)} {token} {format_num(b)} = {format_num(result)}"
                stack_display = str([format_num(x) for x in val_stack._data])
                print(f"{step:<5} {token:<12} {'POP+PUSH':<10} {stack_display:<30} {keterangan}")
                step += 1

    print("─" * 85)
    result = val_stack.pop()
    print(f"✅ Hasil Akhir: {format_num(result)}")
    return result


# ==================== MAIN ====================

def evaluate(expr: str):
    print("\n" + "═" * 65)
    print(f"INPUT EKSPRESI (INFIX): {expr}")
    print("═" * 65)
    try:
        postfix = infix_to_postfix(expr)
        result = evaluate_postfix(postfix)

        print("\n╔══════════════════════════════════╗")
        print("║           RINGKASAN              ║")
        print("╠══════════════════════════════════╣")
        print(f"║ Infix  : {expr:<24}║")
        print(f"║ Postfix: {postfix:<24}║")
        print(f"║ Hasil  : {format_num(result):<24}║")
        print("╚══════════════════════════════════╝")
        return result
    except Exception as e:
        print(f"❌ Error: {e}")


def main():
    print("╔══════════════════════════════════════════════════════════════╗")
    print("║      EVALUATOR EKSPRESI ARITMATIKA MENGGUNAKAN STACK        ║")
    print("║          Struktur Data - Implementasi Array Stack            ║")
    print("╚══════════════════════════════════════════════════════════════╝")
    print("Operator yang didukung: +  -  *  /  ^  dan tanda kurung ( )")
    print("Contoh input: 3 + 4 * 2 / ( 1 - 5 ) ^ 2")
    print()

    test_cases = [
        "3 + 4 * 2",
        "( 1 + 2 ) * ( 3 + 4 )",
        "3 + 4 * 2 / ( 1 - 5 ) ^ 2",
        "10 + 3 * 5 / ( 16 - 4 )"
    ]

    choice = input("Gunakan contoh bawaan? (y/n): ").strip().lower()

    if choice == 'y':
        for expr in test_cases:
            evaluate(expr)
    else:
        expr = input("Masukkan ekspresi infix: ").strip()
        evaluate(expr)


if __name__ == "__main__":
    main()
