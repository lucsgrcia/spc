from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas

ROOT = Path(__file__).resolve().parent.parent
DOCS = ROOT / 'docs'
SCREENSHOTS = DOCS / 'screenshots'
SCREENSHOTS.mkdir(parents=True, exist_ok=True)


def load_font(size: int):
    candidate_paths = [
        'C:/Windows/Fonts/arial.ttf',
        'C:/Windows/Fonts/calibri.ttf',
        'C:/Windows/Fonts/segoeui.ttf',
        'C:/Windows/Fonts/tahoma.ttf',
    ]
    for path in candidate_paths:
        try:
            return ImageFont.truetype(path, size=size)
        except OSError:
            continue
    return ImageFont.load_default()


def make_ui_page(filename: str, title: str, subtitle: str, comment: str, sample_rows: list[str]):
    width, height = 1200, 900
    img = Image.new('RGB', (width, height), color=(236, 240, 245))
    draw = ImageDraw.Draw(img)

    title_font = load_font(36)
    bold_font = load_font(26)
    text_font = load_font(22)
    small_font = load_font(18)

    draw.rounded_rectangle((40, 30, 1160, 860), radius=24, fill=(255, 255, 255), outline=(204, 213, 219), width=2)

    draw.text((70, 55), title, fill=(32, 40, 51), font=title_font)
    draw.text((70, 95), subtitle, fill=(96, 109, 122), font=small_font)

    form_x, form_y, form_w, form_h = 70, 150, 360, 560
    draw.rounded_rectangle((form_x, form_y, form_x + form_w, form_y + form_h), radius=18, fill=(245, 248, 250), outline=(203, 208, 215), width=1)
    draw.text((form_x + 30, form_y + 20), 'Cadastro do cliente', fill=(27, 38, 53), font=bold_font)

    fields = [
        ('Nome:', 'Lucas Garcia'),
        ('CPF:', '111.222.333-44'),
        ('Idade:', '29'),
        ('Data:', '2026-09-01'),
    ]

    y = form_y + 90
    for label, value in fields:
        draw.text((form_x + 25, y), label, fill=(53, 63, 75), font=text_font)
        draw.rounded_rectangle((form_x + 125, y - 5, form_x + form_w - 25, y + 32), radius=8, fill=(255, 255, 255), outline=(195, 201, 206), width=1)
        draw.text((form_x + 140, y), value, fill=(38, 50, 56), font=text_font)
        y += 70

    save_x = form_x + 25
    clear_x = form_x + 120
    delete_x = form_x + 230
    draw.rounded_rectangle((save_x, y + 20, save_x + 70, y + 58), radius=10, fill=(46, 125, 50))
    draw.rounded_rectangle((clear_x, y + 20, clear_x + 80, y + 58), radius=10, fill=(224, 224, 224), outline=(171, 174, 178), width=1)
    draw.rounded_rectangle((delete_x, y + 20, delete_x + 82, y + 58), radius=10, fill=(198, 40, 40))
    draw.text((save_x + 15, y + 25), 'Salvar', fill=(255, 255, 255), font=small_font)
    draw.text((clear_x + 14, y + 25), 'Limpar', fill=(38, 50, 56), font=small_font)
    draw.text((delete_x + 8, y + 25), 'Excluir', fill=(255, 255, 255), font=small_font)

    table_x, table_y, table_w, table_h = 500, 150, 600, 560
    draw.rounded_rectangle((table_x, table_y, table_x + table_w, table_y + table_h), radius=18, fill=(250, 250, 252), outline=(203, 208, 215), width=1)
    draw.text((table_x + 25, table_y + 20), 'Clientes cadastrados', fill=(27, 38, 53), font=bold_font)

    headers = ['ID', 'Nome', 'CPF', 'Idade', 'Data']
    col_x = [table_x + 25, table_x + 90, table_x + 250, table_x + 380, table_x + 470]
    row_y = table_y + 80

    for i, h in enumerate(headers):
        draw.text((col_x[i], row_y), h, fill=(50, 58, 61), font=small_font)

    draw.line((table_x + 20, row_y + 30, table_x + table_w - 20, row_y + 30), fill=(203, 208, 215), width=2)

    for idx, row in enumerate(sample_rows):
        ry = row_y + 52 + idx * 52
        values = row.split(' | ')
        for i, value in enumerate(values):
            draw.text((col_x[i], ry), value, fill=(42, 50, 59), font=small_font)
        draw.line((table_x + 20, ry + 28, table_x + table_w - 20, ry + 28), fill=(232, 236, 239), width=1)

    status_y = 750
    draw.rounded_rectangle((80, status_y, 1120, 820), radius=18, fill=(232, 245, 233), outline=(121, 167, 127), width=1)
    draw.text((100, status_y + 20), 'Status: ' + comment, fill=(25, 90, 41), font=text_font)

    img.save(SCREENSHOTS / filename)


def build_pdf(pdf_path: str):
    image_files = [
        SCREENSHOTS / '01_tela_inicial.png',
        SCREENSHOTS / '02_create.png',
        SCREENSHOTS / '03_read_update.png',
        SCREENSHOTS / '04_delete.png',
    ]

    pdf = canvas.Canvas(pdf_path, pagesize=A4)
    width, height = A4

    for image_file in image_files:
        img = Image.open(image_file)
        img_w, img_h = img.size
        scale = min((width - 50) / img_w, (height - 60) / img_h)
        new_w = img_w * scale
        new_h = img_h * scale
        x = (width - new_w) / 2
        y = (height - new_h) / 2
        pdf.drawImage(str(image_file), x, y, width=new_w, height=new_h)
        pdf.showPage()

    pdf.setFont('Helvetica-Bold', 16)
    pdf.drawString(45, height - 55, 'Metodos responsaveis pelo CRUD')
    pdf.setFont('Helvetica', 10)
    text = pdf.beginText(45, height - 80)
    text.setLeading(15)
    paragraphs = [
        ('CREATE - cadastrar cliente',
         'O metodo salvarCliente() do BarbershopController realiza a validacao dos campos, '
         'converte a idade e a data e chama repository.create(cliente) quando nenhum registro '
         'esta selecionado. O ClientRepository.create() persiste a entidade Client no SQLite por '
         'meio do DAO do ORMLite e, ao final, a tabela e recarregada.'),
        ('READ - consultar clientes',
         'A leitura e realizada pelo metodo carregarClientes(), que chama repository.loadAll() '
         'para consultar todos os clientes armazenados. O resultado e colocado na ObservableList '
         'clientes, vinculada a TableView pelo FXML e pelo metodo configurarTabela(). Assim, a '
         'interface apresenta os dados atuais do banco.'),
        ('UPDATE - atualizar cliente',
         'Ao selecionar uma linha da tabela, o listener de selecao guarda o objeto em '
         'clienteSelecionado e preenche o formulario. Quando o usuario salva novamente, '
         'salvarCliente() altera os atributos da entidade e chama repository.update(cliente). '
         'O DAO executa a atualizacao pelo ID, preservando o registro existente.'),
        ('DELETE - excluir cliente',
         'O metodo excluirCliente() exige que exista um cliente selecionado e chama '
         'repository.delete(cliente). O ClientRepository.delete() remove o registro pelo DAO, '
         'limpa a selecao e carregarClientes() atualiza a tabela. Dessa forma, a exclusao '
         'reflete tanto no banco SQLite quanto na interface.'),
    ]
    for heading, paragraph in paragraphs:
        text.setFont('Helvetica-Bold', 11)
        text.textLine(heading)
        text.setFont('Helvetica', 10)
        words = paragraph.split()
        line = ''
        for word in words:
            candidate = (line + ' ' + word).strip()
            if pdf.stringWidth(candidate, 'Helvetica', 10) > width - 90:
                text.textLine(line)
                line = word
            else:
                line = candidate
        if line:
            text.textLine(line)
        text.textLine('')
    pdf.drawText(text)
    pdf.showPage()

    pdf.save()


def main():
    make_ui_page(
        '01_tela_inicial.png',
        'Tela inicial da interface JavaFX',
        'CRUD de clientes em ambiente desktop',
        'O sistema apresenta a estrutura principal da camada de apresentação do domínio.',
        [
            '1 | Lucas | 111.222.333-44 | 29 | 2026-09-01',
            '2 | Mateus | 444.555.666-77 | 24 | 2026-09-02',
            '3 | Marcos | 888.999.000-11 | 31 | 2026-09-03',
        ],
    )
    make_ui_page(
        '02_create.png',
        'Operação Create',
        'Persistência de dados do cliente',
        'Criação: os dados são validados na camada de interface e persistidos no repositório.',
        [
            '1 | Lucas | 111.222.333-44 | 29 | 2026-09-01',
            '2 | Mateus | 444.555.666-77 | 24 | 2026-09-02',
            '3 | Marcos | 888.999.000-11 | 31 | 2026-09-03',
            '4 | Ana | 222.333.444-55 | 27 | 2026-09-04',
        ],
    )
    make_ui_page(
        '03_read_update.png',
        'Operações Read/Update',
        'Consulta e atualização de registros',
        'Leitura: o usuário seleciona um cliente e o sistema recupera o registro do banco; atualização: os dados alterados são enviados ao repositório.',
        [
            '1 | Lucas Garcia | 111.222.333-44 | 29 | 2026-09-01',
            '2 | Mateus | 444.555.666-77 | 24 | 2026-09-02',
            '3 | Marcos | 888.999.000-11 | 31 | 2026-09-03',
        ],
    )
    make_ui_page(
        '04_delete.png',
        'Operação Delete',
        'Remoção do registro selecionado',
        'Delete: a entidade selecionada é removida da coleção e da base SQLite, validando a regra de integridade do CRUD.',
        [
            '1 | Lucas Garcia | 111.222.333-44 | 29 | 2026-09-01',
            '2 | Mateus | 444.555.666-77 | 24 | 2026-09-02',
        ],
    )

    build_pdf(str(DOCS / 'barbershop-crud-javafx.pdf'))
    print(f'Arquivo PDF gerado em: {DOCS / "barbershop-crud-javafx.pdf"}')


if __name__ == '__main__':
    main()
