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
REPORTTEX = doc/report.tex

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
	$(RM) $(REPORTPDF:.pdf=.log) $(REPORTPDF:.pdf=.aux)		\
		$(REPORTPDF:.pdf=.out) $(REPORTPDF:.pdf=.synctex.gz)
	$(RM) $(REPORTPDF)
