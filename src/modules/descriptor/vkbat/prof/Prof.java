package modules.descriptor.vkbat.prof;

import java.io.File;
import java.io.IOException;

import assist.translation.perl.PerlTranslator;

/**
 * Translation in progress...
 * @translator Benjy Strauss
 *
 */

public class Prof extends PerlTranslator {
	
	private static Par $par;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		$par = new Par();
		/*if( system('pp_popcon_cnt', '-p', 'prof') == -1 ){
			warn("The Rost Lab recommends you install the pp-popularity-contest package that provides pp_popcon_cnt:\n\nsudo apt-get install pp-popularity-contest\n");
		}*/

		//$par.dirHome =                	( $ENV{PROFPHDDIR} || "__PREFIX__/share/profphd" ).'/';
		$par.dirProf = $par.dirHome + "prof/";
		String $ARCH_DEFAULT = "LINUX";
		$par.confProf = $par.dirProf +  "scr/CONFprof.pl";
		$par.packProf = new File($par.dirProf +  "scr/lib/prof.pm");
		
		String $scrName = args[0];
		//$scrName =~ s/^.*\/|\.pl//g; TODO uncomment
		String $scrGoal = "neural network switching";
		String $scrIn = "list_of_files (or single file) parameter_file";
		int $scrNarg= 2;
		// minimal number of input arguments

		String $okFormIn=   "hssp,dssp,msf,saf,fastamul,pirmul,fasta,pir,gcg,swiss";
		String $scrHelpTxt= "Input file formats accepted: \n";
		$scrHelpTxt += "      " +  $okFormIn +"\n";
		
		//$[ =1 ;
		//-e => exists()
		//-l => File is a symbolic link
		if (!$par.packProf.exists() && ! Par.isSymlink($par.packProf)){
		    File $tmp = new File(args[0]);
		    //$tmp=~s/\.pl$/.pm/; TODO uncomment
		    if (!$tmp.exists() && !Par.isSymlink($tmp)){
		    	File $tmp1 = $tmp;
		    	//$tmp1=~s/^.*\///g; TODO uncomment
		    	$par.dirProf += "/";
				//if ($par.dirProf !~ /\/$/) {TODO uncomment
		    		$tmp1 = new File($par.dirProf + $tmp1.getPath());
		    		$tmp=$tmp1;
		    	//}TODO uncomment
		    if (!$tmp.exists() && !Par.isSymlink($tmp)){
			print(
			    "*** ERROR $scrName: could NOT find 'packProf'!\n",
			    "    default is=",$par.packProf.getPath(),"!\n",
			    "    please change this in the top of the file $0!\n",
			    "    note: possible locations are in the prof directory (DIR_PROF):\n",
			    "          DIR_PROF/scr/lib/prof.pm \n",
			    "    or    DIR_PROF/scr/prof.pm \n",
			    "    or    .... :-( sorry no idea what else ... \n");
			}
		    $par.packProf=$tmp;
		}
		String $Lok = ""; //require($par.packProf);TODO uncomment
		if ($Lok == null){
		    print("*** ERROR $scrName: failed to require package packProf=",$par.packProf.getPath(),"!\n");
		}
		String $msg = "";
				/* &prof::full($par.dirHome,$par.dirProf,$par.confProf,$ARCH_DEFAULT,
				   $scrName,$scrGoal,$scrIn,$scrNarg,$okFormIn,$scrHelpTxt,
				   @ARGV);
				   TODO uncomment*/
		$Lok = $msg;
		if ($Lok == null){
		    print("*** ERROR $scrName: after package prof:full (",$par.packProf.getPath(),")\n",$msg,"\n");
		}
		}

		/*=pod

		=head1 NAME

		prof - secondary structure and solvent accessibility predictor

		=head1 SYNOPSIS

		prof [F<INPUTFILE>+] [OPTIONS]

		=head1 DESCRIPTION

		Secondary structure is predicted by a system of neural networks rating at an expected average accuracy > 72% for the three states helix, strand and loop (Rost & Sander, PNAS, 1993 , 90, 7558-7562; Rost & Sander, JMB, 1993 , 232, 584-599; and Rost & Sander, Proteins, 1994 , 19, 55-72; evaluation of accuracy). Evaluated on the same data set, PROFsec is rated at ten percentage points higher three-state accuracy than methods using only single sequence information, and at more than six percentage points higher than, e.g., a method using alignment information based on statistics (Levin, Pascarella, Argos & Garnier, Prot. Engng., 6, 849-54, 1993).
		PHDsec predictions have three main features:

		=over

		=item 1. improved accuracy through evolutionary information from multiple sequence alignments

		=item 2. improved beta-strand prediction through a balanced training procedure

		=item 3. more accurate prediction of secondary structure segments by using a multi-level system

		=back

		Solvent accessibility is predicted by a neural network method rating at a correlation coefficient (correlation between experimentally observed and predicted relative solvent accessibility) of 0.54 cross-validated on a set of 238 globular proteins (Rost & Sander, Proteins, 1994, 20, 216-226; evaluation of accuracy). The output of the neural network codes for 10 states of relative accessibility. Expressed in units of the difference between prediction by homology modelling (best method) and prediction at random (worst method), PROFacc is some 26 percentage points superior to a comparable neural network using three output states (buried, intermediate, exposed) and using no information from multiple alignments.

		Transmembrane helices in integral membrane proteins are predicted by a system of neural networks. The shortcoming of the network system is that often too long helices are predicted. These are cut by an empirical filter. The final prediction (Rost et al., Protein Science, 1995, 4, 521-533; evaluation of accuracy) has an expected per-residue accuracy of about 95%. The number of false positives, i.e., transmembrane helices predicted in globular proteins, is about 2%.
		The neural network prediction of transmembrane helices (PHDhtm) is refined by a dynamic programming-like algorithm. This method resulted in correct predictions of all transmembrane helices for 89% of the 131 proteins used in a cross-validation test; more than 98% of the transmembrane helices were correctly predicted. The output of this method is used to predict topology, i.e., the orientation of the N-term with respect to the membrane. The expected accuracy of the topology prediction is > 86%. Prediction accuracy is higher than average for eukaryotic proteins and lower than average for prokaryotes. PHDtopology is more accurate than all other methods tested on identical data sets. 

		If no output file option (such as B<--fileRdb> or B<--fileOut>) is given the RDB formatted output is written into F<./INPUTFILENAME.prof> where 'prof' replaces the extension of the input file.  In lack of extension '.prof' is appended to the input file name.

		=head2 Output format

		The RDB format is self-annotating, see example outputs in F<__pkgdatadir__/prof/exa>.

		=head1 REFERENCES

		=over

		=item Rost, B. and Sander, C. (1994a). Combining evolutionary information and neural networks to predict protein secondary structure. Proteins, 19(1), 55-72.

		=item Rost, B. and Sander, C. (1994b). Conservation and prediction of solvent accessibility in protein families. Proteins, 20(3), 216-26.

		=item Rost, B., Casadio, R., Fariselli, P., and Sander, C. (1995). Transmembrane helices predicted at 95% accuracy. Protein Sci, 4(3), 521-33.

		=back

		=head1 OPTIONS

		See each keyword for more help.  Most of these are likely to be broken.

		=over

		=item a

		alternative connectivity patterns (default=3)

		=item Z<>3

		predict sec + acc + htm

		=item acc

		predict solvent accessibility, only

		=item ali             

		add alignment to 'human-readable' PROF output file(s)

		=item arch   

		system architecture (e.g.: SGI64|SGI5|SGI32|SUNMP|ALPHA)

		=item ascii          

		write 'human-readable' PROF output file(s)

		=item best   

		PROF with best accuracy and longest run-time

		=item both

		predict secondary structure and solvent accessibility

		=item data           

		data=<all|brief|normal|detail>  for HTML out: only those parts of predictions written

		=item debug          

		keep most intermediate files, print debugging messages

		=item dirWork

		work directory, default: a temporary directory from File::Temp::tempdir. Must be fully qualified path.

		Known to work.

		=item doEval

		DO evaluation for list (only for known structures and lists)

		=item doFilterHssp    

		filter the input HSSP file       (excluding some pairs)

		=item doHtmfil        	

		DO filter the membrane prediction                  (default)

		=item doHtmisit

		DO check strength of predicted membrane helix      (default)

		=item doHtmref

		DO refine the membrane prediction                  (default)

		=item doHtmtop

		DO membrane helix topology                         (default)

		=item dssp

		convert PROF into DSSP format

		=item expand

		expand insertions when converting output to MSF format

		=item fast 

		PROF with lowest accuracy and highest speed

		=item fileCasp

		name of PROF output in CASP format              (file.caspProf)

		=item fileDssp

		name of PROF output in DSSP format              (file.dsspProf)

		=item fileHtml

		name of PROF output in HTML format              (file.htmlProf)

		=item fileMsf

		name of PROF output in MSF format               (file.msfProf)

		=item fileNotHtm

		name of file flagging that no membrane helix was found

		=item fileOut

		name of PROF output in RDB format               (file.rdbProf)

		Known to work.

		=item fileProf

		name of PROF output in human readable format    (file.prof)

		Broken.

		=item fileRdb

		name of PROF output in RDB format               (file.rdbProf)

		Known to work.

		=item fileSaf         

		name of PROF output in SAF format               (file.safProf)

		=item filter

		filter the input HSSP file       (excluding some pairs)

		=item good

		PROF with good accuracy and moderate speed

		=item graph

		add ASCII graph to 'human-readable' PROF output file(s)

		=item htm

		use: 'htm=<N|0.N>' gives minimal transmembrane helix detected default is 'htm=8' (resp. htm=0.8)  smaller numbers more false positives and fewer false negatives!

		=item html   argument         

		'hmtl' or 'html=<all|body|head>' write HTML format of prediction 'html' will result in that the PROF output is converted to HTML 'html=body' restricts HTML file to the HTML_BODY tag part 'html=head' restricts HTML file to the HTML_HEADER tag part 'html=all'  gives both HEADER and BODY

		=item keepConv

		keep the conversion of the input file to HSSP format

		=item keepFilter argument

		<*|doKeepFilter=1>     keep the filtered HSSP file

		=item keepHssp  argument

		<*|doKeepHssp=1>         keep the intermediate HSSP file

		=item keepNetDb argument

		<*|doKeepNetDb=1>       keep the intermediate DbNet file(s)

		=item list argument

		<*|isList=1>      input file is list of files

		=item msf   

		convert PROF into MSF format

		=item nice            

		give 'nice-D' to set the nice value (priority) of the job

		=item noProfHead

		do NOT copy file with tables into local directory

		=item noSearch

		short for doSearchFile=0, i.e. no searching of DB files

		=item noascii

		surpress writing ASCII (i.e. human readable) result files

		=item nohtml

		surpress writing HTML result files

		=item nonice

		job will not be niced, i.e. not run with lower priority

		=item notEval

		DO NOT check accuracy even when known structures

		=item notHtmfil

		do NOT filter the membrane prediction

		=item notHtmisit      

		do NOT check whether or not membrane helix strong enough

		=item notHtmref

		do NOT refine the membrane prediction

		=item notHtmtop

		do NOT membrane helix topology

		=item nresPerLineAli

		Number of characters used for MSF file. Default: 50.

		=item numresMin

		Minimal number of residues to run network, otherwise prd=symbolPrdShort. Default: 9.

		=item optJury

		Adds PHD to jury. Default: `normal,usePHD'.

		Many other parameters change the default for this one as a side-effect, the list is not comprehensive:

		phd, nophd, /^para(3|Both|Sec|Acc|Htm|CapH|CapE|CapHE)/, /^para?/, jct 

		=item para3

		Parameter file for sec+acc+htm. Default: `<DIRPROF>/net/PROFboth_best.par'.

		=item paraAcc

		Parameter file for acc. Default: `<DIRPROF>/net/PROFacc_best.par'.

		=item paraBoth

		Parameter file for sec+acc. Default: `<DIRPROF>/net/PROFboth_best.par'.

		=item paraSec

		Parameter file for sec. Default: `<DIRPROF>/net/PROFsec_best.par'.

		=item riSubAcc

		Minimal reliability index (RI) for subset PROFacc. Default: 4.

		=item riSubSec

		Minimal reliability index (RI) for subset PROFsec. Default: 5.

		=item riSubSym

		Symbol for residues predicted with RI < riSubSec/Acc. Default: `.'.

		=item s_k_i_p

		problems, manual, hints, notation, txt, known, DONE, Date, date, aa, Lhssp, numaa, code

		=item saf

		convert PROF into SAF format

		=item scrAddHelp      

		=item scrGoal

		neural network switching

		=item scrHelpTxt

		Input file formats accepted:       hssp,dssp,msf,saf,fastamul,pirmul,fasta,pir,gcg,swiss

		=item scrIn

		list_of_files (or single file) parameter_file

		=item scrName

		prof

		=item scrNarg

		2

		=item sec

		predict secondary structure,   only

		=item silent

		no information written to screen - this is the default

		=item skipMissing	

		do not abort if input file missing!

		=item sourceFile

		prof

		=item test

		is just a test (faster)

		=item translate-jobid-in-param-values

		String 'jobid' gets substituted with $par{jobid}

		=item tst

		quick run through program, low accuracy

		=item user

		user name

		=item --version

		Print version

		=back

		=head1 AUTHOR

		B. Rost, Sander C, Fariselli P, Casadio R, Liu J, Yachdav G, Kajan L.

		=head1 EXAMPLES

		=over

		=item Prediction from alignment in HSSP file for best results

		 prof __PREFIX__/share/profphd/prof/exa/1ppt.hssp fileRdb=/tmp/1ppt.hssp.prof

		=item Prediction from a single sequence

		 prof __PREFIX__/share/profphd/prof/exa/1ppt.f fileRdb=/tmp/1ppt.f.rdbProf

		=item phd.pl invocation

		 __PREFIX__/share/profphd/prof/embl/phd.pl __PREFIX__/share/profphd/prof/exa/1ppt.hssp htm fileOutPhd=/tmp/query.phdPred  fileOutRdb=/tmp/query.phdRdb  fileNotHtm=/tmp/query.phdNotHtm

		=back

		=head1 ENVIRONMENT

		=over

		=item PROFPHDDIR

		Override package prof package dir F<__PREFIX__/share/profphd>.

		=item RGUTILSDIR

		Override location of librg-utils-perl F<__PREFIX__/share/librg-utils-perl>.

		=back

		=head1 FILES

		=over

		=item F<*.rdbProf>

		default output file extension

		=item F<__PREFIX__/share/profphd/prof>

		default data directory

		=back

		=head1 BUGS

		Please report bugs at L<https://rostlab.org/bugzilla3/enter_bug.cgi?product=profphd>.

		=over

		=item Prediction from HSSP file fails when residue lines with exclamation marks `!' are present:

		Use 'optJury=normal' and 'both' like this:

		 prof /tmp/1a3q.hssp fileRdb=/tmp/1a3q.hssp.profRdb optJury=normal both

		=back

		=head1 SEE ALSO

		=over

		=item Main website

		L<http://www.predictprotein.org/>

		=item Documentation

		L<http://www.predictprotein.org/docs.php>

		=item Community website

		L<http://groups.google.com/group/PredictProtein>

		=item FTP

		L<ftp://rostlab.org/pub/cubic/downloads/prof>

		=item Newsgroups

		L<http://groups.google.com/group/PredictProtein>

		=back

		=cut*/
	}
}
