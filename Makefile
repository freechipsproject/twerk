# Top level makefile 
# Boris V.Kuznetsov
# Neurodyne Systems
# Feb 14 1019

OUTDIR=./out
GRIDHOME=$(OUTDIR)/grid

view:
	@gtkwave $(GRIDHOME)/*.vcd grid.gtkw &

# Clean options
clean:
	@rm -rf $(OUTDIR) test_run_dir

allclean:clean
	@find . -name "target" | xargs rm -rf {} \;

