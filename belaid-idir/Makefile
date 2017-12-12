SHELL = /bin/sh
RM = rm -f

DIST = projet-protint

JAVAC = javac
JAVACFLAGS = -g -Xlint -Xlint:-serial -deprecation

javacompile = $(JAVAC) $(JAVACFLAGS)
srcdir = src
dirs = $(addprefix $(srcdir)/,common server client)
destdir = classes

PDFLATEX = pdflatex
PDFLATEXFLAGS = -interaction nonstopmode -file-line-error -synctex=1
REPORTPDF = report.pdf
REPORTTEX = report.tex

texcompile = $(PDFLATEX) $(PDFLATEXFLAGS)

.PHONY: all dist clean

all:
	mkdir -p $(destdir)
	$(javacompile) -d $(destdir) $(foreach dir,$(dirs),$(dir)/*.java)

$(REPORTPDF): $(REPORTTEX)
	$(texcompile) $<
	$(texcompile) $<

dist: clean $(REPORTPDF)
	tar -cvzf $(DIST).tgz						\
		--exclude-backups --exclude="\#*\#" --exclude=".\#*"	\
		Makefile README client server $(REPORTPDF) $(srcdir)

clean:
	$(RM) -R $(destdir)
	$(RM) $(REPORTTEX:.tex=.log) $(REPORTTEX:.tex=.aux)		\
		$(REPORTTEX:.tex=.out) $(REPORTTEX:.tex=.synctex.gz)
